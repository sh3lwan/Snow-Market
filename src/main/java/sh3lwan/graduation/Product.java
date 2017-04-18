package sh3lwan.graduation;

import java.io.Serializable;

/**
 * Created by shabon on 10/14/2016.
 */

public class Product implements Serializable {
    private int ID;
    private String name;
    private float rate;
    private String price;
    private String secondPrice;
    private String imageURL;
    private String description;
    private int reviews;

    public Product(int id, int reviews, String name, float rate, String price, String secondPrice, String imageURL) {
        this.reviews = reviews;
        this.name = name;
        this.rate = rate;
        this.ID = id;
        this.price = price;
        this.secondPrice = secondPrice;
        this.imageURL = imageURL;
    }

    public Product(int id, int reviews, String description, String name, float rate, String price, String secondPrice, String imageURL) {
        this.ID = id;
        this.description = description;
        this.name = name;
        this.reviews = reviews;
        this.rate = rate;
        this.price = price;
        this.secondPrice = secondPrice;
        this.imageURL = imageURL;
    }

    public int getReviews() {
        return reviews;
    }

    public String getDescription() {
        return description;
    }

    public int getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getSecondPrice() {
        return secondPrice;
    }

    public void setSecondPrice(String secondPrice) {
        this.secondPrice = secondPrice;
    }
}
