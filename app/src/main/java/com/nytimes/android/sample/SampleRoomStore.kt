package com.nytimes.android.sample

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Database
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Insert
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.Query
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.impl.room.RoomInternalStore
import com.nytimes.android.external.store3.base.room.RoomPersister
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single


@Entity
data class User(
        @PrimaryKey(autoGenerate = true)
        var uid: Int = 0,
        val name: String)

@Dao
interface UserDao {
    @Query("SELECT name FROM user")
    fun loadAll(): Flowable<List<String>>

    @Insert
    fun insertAll(vararg users: User)

}

@Database(entities = arrayOf(User::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}

val db = Room.databaseBuilder(SampleApp.appContext!!, AppDatabase::class.java, "db").build()


val store = RoomInternalStore(
        Fetcher<User, String> { Single.just(User(name = "Mike")) },
        object : RoomPersister<User, List<String>, String> {

            override fun read(key: String): Observable<List<String>> {
                return db.userDao().loadAll().toObservable()
            }

            override fun write(key: String, user: User) {
                db.userDao().insertAll(user)
            }
        })


