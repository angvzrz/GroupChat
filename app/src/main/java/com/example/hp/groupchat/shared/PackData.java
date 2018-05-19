package com.example.hp.groupchat.shared;


import java.io.Serializable;
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
    private int width;
    private int height;
    private int size;
    private byte[] content;
    private final String time;
    private char position;

    public PackData(String from, String type, String text) {

        this.time = ServerUtils.dateLog();
        this.from = from;
        this.type = type;
        this.text = text;
        this.position = 'E';
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


    public byte[] getContent() {
        return content;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public void setType (String type) {
        this.type = type;
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

    public char getPosition() {
        return position;
    }

    public void setPosition(char position) {
        this.position = position;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.from);
        hash = 79 * hash + this.port_ID;
        hash = 79 * hash + Objects.hashCode(this.type);
        hash = 79 * hash + Objects.hashCode(this.text);
        hash = 79 * hash + Objects.hashCode(this.file_name);
        hash = 79 * hash + this.width;
        hash = 79 * hash + this.height;
        hash = 79 * hash + this.size;
        hash = 79 * hash + Arrays.hashCode(this.content);
        hash = 79 * hash + Objects.hashCode(this.time);
        hash = 79 * hash + this.position;
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
        if (!Objects.equals(this.type, other.type)) {
            return false;
        }
        return Objects.equals(this.text, other.text);
    }


    @Override
    public String toString() {
        //To change body of generated methods, choose Tools | Templates.
        return String.format("%s - %s; %s %s", time, from, type, text);
    }
}
