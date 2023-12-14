/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fabula.model.document;

import com.fabula.model.accounts.User;
import com.fabula.model.authorization.IResource;
import com.fabula.model.domain.Domain;
import com.fabula.model.file.File;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author caio
 */
@Entity
public class Document implements IResource {

    @Id
    @GeneratedValue
    private UUID id;

    private String title;
    @ManyToOne
    private File titleImage;
    private String subtitle;
    @Column(columnDefinition="TEXT")
    private String contents;
    @ManyToOne
    @JoinColumn(name = "domain")
    Domain domain;
    @ManyToOne
    private User author;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime creationDateTime;

//    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL)
//    Set<Authorship> autorships;
    boolean restricted;

    public Document(String title, String subtitle, String contents, boolean isPrivate, Domain domain) {
        this.title = title;
        this.subtitle = subtitle;
        this.contents = contents;
        this.creationDateTime = LocalDateTime.now();
        this.restricted = isPrivate;
        this.domain = domain;
    }

    public Document(UUID id) {
        this.id = id;
    }

    public Document() {

    }

    public boolean isRestricted() {
        return restricted;
    }

    public void setRestricted(boolean restricted) {
        this.restricted = restricted;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the subtitle
     */
    public String getSubtitle() {
        return subtitle;
    }

    /**
     * @param subtitle the subtitle to set
     */
    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    /**
     * @return the contents
     */
    public String getContents() {
        return contents;
    }

    /**
     * @param contents the contents to set
     */
    public void setContents(String contents) {
        this.contents = contents;
    }

    public void appendContents(String contents) {
        this.contents += contents;
    }

    public LocalDateTime getCreationDateTime() {
        return this.creationDateTime;
    }

    public Domain getDomain() {
        return this.domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.id);
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
        final Document other = (Document) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    public UUID getTitleImage(){
        if(this.titleImage!=null){
            return this.titleImage.getId();
        }
        else{
            return null;
        }
    }
    
//    @JsonIgnore
//    public File getTitleImage() {
//        return this.titleImage;
//    }

    public void setTitleImage(File titleImage) {
        this.titleImage = titleImage;
    }

    public User getAuthor() {
        return this.author;
    }

    public LocalDateTime getCreationDate() {
        return this.creationDateTime;
    }

}
