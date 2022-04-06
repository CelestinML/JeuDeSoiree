package com.example.schlouky;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

public class Player implements Parcelable
{
    String name;
    Boolean buveur;
    String photoPath;

    Player(String name, Boolean buveur, String photoPath)
    {
        this.name = name;
        this.buveur = buveur;
        this.photoPath = photoPath;
    }

    protected Player(Parcel in) {
        name = in.readString();
        byte tmpBuveur = in.readByte();
        buveur = tmpBuveur == 0 ? null : tmpBuveur == 1;
        photoPath = in.readString();
    }

    public static final Creator<Player> CREATOR = new Creator<Player>() {
        @Override
        public Player createFromParcel(Parcel in) {
            return new Player(in);
        }

        @Override
        public Player[] newArray(int size) {
            return new Player[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeByte((byte) (buveur == null ? 0 : buveur ? 1 : 2));
        parcel.writeString(photoPath);
    }
}
