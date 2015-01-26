/**
 * Copyright 2012 BlueOxygen Technology

 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0 
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mutiaraiman.droid;

import java.io.Serializable;

/**
 * @author Dian Aditya
 * 
 */
public class Prayer implements Serializable {

    private static final long serialVersionUID = 5004264790617673970L;
    
    private int id;
    private String title;
    private String content;
    private String reminder;
    private int status;
    private String createDate;
    private int type = 0;
    private boolean alreadyRead;

    public Prayer() {
        
    }

    public Prayer(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReminder() {
        return reminder;
    }

    public void setReminder(String reminder) {
        this.reminder = reminder;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isAlreadyRead() {
        return alreadyRead;
    }

    public void setAlreadyRead(boolean alreadyRead) {
        this.alreadyRead = alreadyRead;
    }

}
