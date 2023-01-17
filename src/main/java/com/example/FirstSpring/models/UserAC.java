package com.example.FirstSpring.models;

import javax.persistence.*;

@Entity
@Table(name = "t_user_ac")
public class UserAC {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ac_id")
    private AC AC;

    public UserAC(User user, AC AC, Double count) {
        this.user = user;
        this.AC = AC;
        this.count = count;
    }


    public UserAC(){

    }
    private Double count;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public AC getAC() {
        return AC;
    }

    public void setAC(AC AC) {
        this.AC = AC;
    }

    public Double getCount() {
        return count;
    }

    public void setCount(Double count) {
        this.count = count;
    }
}
