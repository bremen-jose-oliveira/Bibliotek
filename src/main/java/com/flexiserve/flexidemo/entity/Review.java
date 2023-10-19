package com.flexiserve.flexidemo.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import jakarta.persistence.*;

@Entity
@Table(name="Reviews")
public class Review {


        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id")
        private  int id;
        @Column(name = "rating")
        private  String rating;
        @Column(name = "comment")
        private  String comment;

        @Column(name = "user_id")
        private  int userId;

    public Review() {
    }

        public Review(String rating, String comment, int userId) {
                this.rating = rating;
                this.comment = comment;
                this.userId = userId;
        }

        public int getId() {
                return id;
        }

        public void setId(int id) {
                this.id = id;
        }

        public String getRating() {
                return rating;
        }

        public void setRating(String rating) {
                this.rating = rating;
        }

        public String getComment() {
                return comment;
        }

        public void setComment(String comment) {
                this.comment = comment;
        }

        public int getUserId() {
                return userId;
        }

        public void setUserId(int userId) {
                this.userId = userId;
        }

        @Override
        public String toString() {
                return "Review{" +
                        "id=" + id +
                        ", rating='" + rating + '\'' +
                        ", comment='" + comment + '\'' +
                        ", userId=" + userId +
                        '}';
        }
}
