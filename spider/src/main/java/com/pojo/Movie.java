package com.pojo;

/**
 * @author 江
 */
public class Movie {
    private String name;
    private String account;
    private String introduce;
    private String language;
    private String showing;
    private String category;
    private String image;

    @Override
    public String toString() {
        return "Movie{" +
                "name='" + name + '\'' +
                ", account='" + account + '\'' +
                ", introduce='" + introduce + '\'' +
                ", language='" + language + '\'' +
                ", showing='" + showing + '\'' +
                ", category='" + category + '\'' +
                ", image='" + image + '\'' +
                '}';
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getShowing() {
        return showing;
    }

    public void setShowing(String showing) {
        this.showing = showing;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
