package com.github.albion.rest;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("User")
public class User extends ResourceSupport
{

    @Id
    String name;
    String email;
    String password;
    String server;

    @JsonCreator
    public User(@JsonProperty("name") String name, @JsonProperty("email") String email, @JsonProperty("password") String password, @JsonProperty("server") String server)
    {
        this.name = name;
        this.email = email;
        this.password = password;
        this.server = server;
    }

    public String getName()
    {
        return name;
    }

    public String getPassword()
    {
        return password;
    }

    public String getEmail()
    {
        return email;
    }

    public String getServer()
    {
        return server;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public void setServer(String server)
    {
        this.server = server;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this);
    }

}
