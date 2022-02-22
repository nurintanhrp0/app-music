package com.example.greendaogenerator;

import org.greenrobot.greendao.generator.DaoGenerator;
import org.greenrobot.greendao.generator.Entity;
import org.greenrobot.greendao.generator.Schema;

public class MainGenerator {
    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, "com.example.musicapps.database");

        //region Music Entity
        Entity track = schema.addEntity("TTrack");
        track.addIdProperty();
        track.addLongProperty("TrackId").notNull();
        track.addStringProperty("TrackName").notNull();
        track.addStringProperty("ArtisName").notNull();
        track.addIntProperty("IsFavorite").notNull();
        track.addIntProperty("IsLoading").notNull();
        //endregion

        new DaoGenerator().generateAll(schema, "../app/src/main/java");
    }
}