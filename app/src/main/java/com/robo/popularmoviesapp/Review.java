package com.robo.popularmoviesapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by robo on 7/12/15.
 */
public class Review implements Parcelable {

    public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>() {
        public Review createFromParcel(Parcel source) {
            return new Review(source);
        }

        public Review[] newArray(int size) {
            return new Review[size];
        }
    };
    public String author;
    public String content;

    public Review(String author, String content) {
        this.author = author;
        this.content = content;
    }

    public Review() {
    }

    protected Review(Parcel in) {
        this.author = in.readString();
        this.content = in.readString();
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.author);
        dest.writeString(this.content);
    }
}
