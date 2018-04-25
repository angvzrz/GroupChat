package com.example.hp.groupchat.shared;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Objects;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Objects;

/**
 *
 * @author hp
 */
public class PackData implements Serializable {

    private String from;
    private int port_ID;

    private String type;
    private String text;
    private String file_name;
    private int length;
    private byte[] content;
    private final String time;
    private char pos;

    public PackData(String from, String type, String text) {

        this.time = ServerUtils.dateLog();
        this.from = from;
        this.type = type;
        this.text = text;
        this.pos = 'E';
    }

    public String getFrom() {
        return from;
    }

    public String getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public String getFile_name() {
        return file_name;
    }

    public int getLength() {
        return length;
    }

    public byte[] getContent() {
        return content;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public int getPort_ID() {
        return port_ID;
    }

    public void setPort_ID(int port_ID) {
        this.port_ID = port_ID;
    }

    public String getTime() {
        return time;
    }

    public char getPos() {
        return pos;
    }

    public void setPos(char pos) {
        this.pos = pos;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + Objects.hashCode(this.from);
        hash = 23 * hash + this.port_ID;
        hash = 23 * hash + Objects.hashCode(this.type);
        hash = 23 * hash + Objects.hashCode(this.text);
        hash = 23 * hash + Objects.hashCode(this.file_name);
        hash = 23 * hash + this.length;
        hash = 23 * hash + Arrays.hashCode(this.content);
        hash = 23 * hash + Objects.hashCode(this.time);
        hash = 23 * hash + this.pos;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PackData other = (PackData) obj;
        if (this.port_ID != other.port_ID) {
            return false;
        }
        if (!Objects.equals(this.from, other.from)) {
            return false;
        }
        if (!Objects.equals(this.type, other.type)) {
            return false;
        }
        return Objects.equals(this.time, other.time);
    }

    @Override
    public String toString() {
        return String.format("%s - %s; %s %s", time, from, type, text); //To change body of generated methods, choose Tools | Templates.
    }

}
