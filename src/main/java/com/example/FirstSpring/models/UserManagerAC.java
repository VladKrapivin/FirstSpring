package com.example.FirstSpring.models;

import javax.persistence.*;

@Entity
@Table(name = "t_user_manager")
public class UserManagerAC {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manager_id")
    private User manager;

    public UserManagerAC() {
    }

    public UserManagerAC(User user, User manager, AC AC) {
        this.user = user;
        this.manager = manager;
        this.AC = AC;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ac_id")
    private AC AC;

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

    public User getManager() {
        return manager;
    }

    public void setManager(User manager) {
        this.manager = manager;
    }

    public AC getAC() {
        return AC;
    }

    public void setAC(AC AC) {
        this.AC = AC;
    }
}
