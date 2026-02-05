package com.ruriboshi.habitter

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class CustomApplication :Application(){
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)

        val config = RealmConfiguration.Builder()
            .allowWritesOnUiThread(true)
            .allowQueriesOnUiThread(true)
            .build()

        Realm.setDefaultConfiguration(config)

        /*val realmConfig = RealmConfiguration.Builder() // Realmの設定を定義
            .schemaVersion(1L) // 新しいスキーマのバージョンを設定
            .migration(MyMigration()) // マイグレーション用のコードを設定
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.setDefaultConfiguration(realmConfig)*/

        /*.allowWritesOnUiThread(true)
            .allowQueriesOnUiThread(true)*/
    }
}