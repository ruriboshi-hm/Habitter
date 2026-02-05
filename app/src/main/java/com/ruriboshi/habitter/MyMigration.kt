package com.ruriboshi.habitter

import io.realm.DynamicRealm
import io.realm.RealmMigration
import io.realm.RealmSchema


class MyMigration: RealmMigration {
    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        var version:Long = oldVersion
        val schema: RealmSchema = realm.schema
        /*Log.d("migration",version.toString())
        if (version == 0L) {
            schema.get("MyModel")!!
                .addField("idolName", String::class.java,FieldAttribute.REQUIRED)
            version++
            //Log.d("successMigration",version.toString())
        }
        if (version == 1L){
            schema.get("MyModel")!!
                .transform { obj: DynamicRealmObject ->
                val name = obj.getBlob("image")
                obj.setBlob("iconImage" , name) }
            version++
            Log.d("successMigration",version.toString())
        }*/

        /*if (version == 1L){
            schema.get("MyModel")!!
                .addField("sample",Long::class.java)
            version+=1L
        }

        if (version == 2L){
            schema.get("MyModel")!!
                .addField("sample2",Long::class.java)
            version+=1L
        }*/
        //↑これならいける！！
    }
}