package com.github.albion.rest;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("Server")
public class Server extends ResourceSupport
{

     @Id String name;
     String address;
     String port;

    @JsonCreator
    public Server(@JsonProperty("name") String name, @JsonProperty("address") String address, @JsonProperty("port") String port)
    {
        this.name = name;
        this.address = address;
        if (port != null && !StringUtils.isEmpty(port))
        {
            this.port = port;
        } else
        {
            this.port = "25565";
        }
    }

    public String getName()
    {
        return name;
    }

    public String getPort()
    {
        return port;
    }

    public String getAddress()
    {
        return address;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this);
    }
}
