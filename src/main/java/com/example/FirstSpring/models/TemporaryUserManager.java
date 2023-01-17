package com.example.FirstSpring.models;

import javax.persistence.*;

@Entity
@Table(name = "temporary_user_manager")
public class TemporaryUserManager {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "AC_id")
    private AC AC;

    public TemporaryUserManager() {
    }

    public TemporaryUserManager(User user, AC AC) {
        this.user = user;
        this.AC = AC;
    }

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
}
