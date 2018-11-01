package com.udacity.sanketbhat.news4you.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "article_type",
        primaryKeys = {"id", "type"},
        foreignKeys = {@ForeignKey(entity = Article.class, parentColumns = {"id"}, childColumns = {"id"}, onDelete = CASCADE)},
        indices = {@Index(value = {"id", "type"}, unique = true)})
public class ArticleType {
    private int id;
    private int type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public static final class Type {
        public static final int TOP_HEAD = 0;
        static final int BUSINESS = 1;
        static final int ENTERTAINMENT = 2;
        static final int GENERAL = 4;
        static final int HEALTH = 8;
        static final int SCIENCE = 16;
        static final int SPORTS = 32;
        static final int TECHNOLOGY = 64;

        public static final int[] types = {BUSINESS, ENTERTAINMENT, HEALTH, SCIENCE, SPORTS, TECHNOLOGY};

        public static String getName(int type) {
            if (type == TOP_HEAD) {
                return "Top Headlines";
            } else if (type == BUSINESS) {
                return "Business";
            } else if (type == ENTERTAINMENT) {
                return "Entertainment";
            } else if (type == GENERAL) {
                return "General";
            } else if (type == HEALTH) {
                return "Health";
            } else if (type == SCIENCE) {
                return "Science";
            } else if (type == SPORTS) {
                return "Sports";
            } else if (type == TECHNOLOGY) {
                return "Technology";
            } else {
                return "Unknown";
            }
        }
    }
}
