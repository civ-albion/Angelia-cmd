package com.github.albion.rest;

import com.github.albion.rest.listener.ChatListener;
import com.github.albion.rest.listener.PlayerStateListener;
import com.github.maxopoly.angeliacore.SessionManager;
import com.github.maxopoly.angeliacore.connection.ActiveConnectionManager;
import com.github.maxopoly.angeliacore.connection.DisconnectReason;
import com.github.maxopoly.angeliacore.connection.ServerConnection;
import com.github.maxopoly.angeliacore.connection.login.AuthenticationHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BotController
{

    static final Server UNKNOWN_SERVER = new Server("UNKNOWN", "NO ADDRESS", "0");
    static final User UNKNOWN_USER = new User("UNKNOWN", "NO EMAIL", "NO PASSWORD", "UNKNOWN");
    static Logger LOGGER = LogManager.getLogger("app");

    SessionManager sessionManager;

    @Autowired
    UserRepository userRepo;
    @Autowired
    ServerRepository serverRepo;

    @PostConstruct
    public void initialize()
    {
        sessionManager = new SessionManager(LOGGER, true);
        List<Server> servers = serverRepo.findAll();
        Map<String, Server> serverMap = servers.stream().collect(Collectors.toMap(s -> s.getName(), s -> s));
        List<User> users = userRepo.findAll();
        users.stream().forEach(user -> LOGGER.error(user));
        serverMap.entrySet().stream().forEach(server -> LOGGER.error(server));
        users.stream()
                .filter(u -> serverMap.containsKey(u.getServer()))
                .forEach(u ->
                {
                    try {
                    manageConnection(u, serverMap.get(u.getServer()));
                    LOGGER.error("GOT ONE" + u.toString());
                    } catch (Exception ex) {
                        LOGGER.error("PROBLEM!!!!", ex);
                    }
            try
            {
                Thread.sleep(20000l);
            } catch (InterruptedException ex)
            {
                java.util.logging.Logger.getLogger(BotController.class.getName()).log(Level.SEVERE, null, ex);
            }
                });
    }

    @PutMapping("/servers/{serverid}")
    public ResponseEntity<Server> handleAddServer(@PathVariable(value = "serverid", required = true) String serverid, @RequestBody Server server)
    {
        serverRepo.save(server);
        return new ResponseEntity<>(server, HttpStatus.OK);
    }

    @DeleteMapping("/servers/{serverid}")
    public ResponseEntity<String> handleRemoveServer(@PathVariable(value = "serverid", required = true) String serverid)
    {
        serverRepo.deleteById(serverid);
        return new ResponseEntity<>(serverid, HttpStatus.OK);
    }

    @GetMapping("/servers/{serverid}")
    public ResponseEntity<Server> handleGetServer(@PathVariable(value = "serverid", required = true) String serverid)
    {
        return new ResponseEntity<>(serverRepo.findById(serverid).orElse(UNKNOWN_SERVER), HttpStatus.OK);
    }

    @GetMapping("/servers")
    public ResponseEntity<List<Server>> handleGetServer()
    {
        List<Server> response = new ArrayList<>();
        serverRepo.findAll().forEach(response::add);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> handleGetUsers()
    {
        List<User> response = new ArrayList<>();
        userRepo.findAll().forEach(response::add);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/users/{userid}")
    public ResponseEntity<User> handleAddUser(@PathVariable(value = "userid", required = true) String userid, @RequestBody User user)
    {
        User response = userRepo.insert(user);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/users/{userid}")
    public ResponseEntity<String> handleRemoveUser(@PathVariable(value = "userid", required = true) String userid)
    {
        userRepo.deleteById(userid);
        return new ResponseEntity<>(userid, HttpStatus.OK);
    }

    @GetMapping("/users/{userid}")
    public ResponseEntity<User> handleGetUser(@PathVariable(value = "userid", required = true) String userid)
    {
        User response = userRepo.findById(userid).orElse(UNKNOWN_USER);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/users/{userid}/servers/{serverid}")
    public ResponseEntity<User> handleAddUserToServer(
            @PathVariable(value = "userid", required = true) String userid,
            @PathVariable(value = "serverid", required = true) String serverid)
    {
        final ResponseEntity<User> response;
        Optional<User> maybeUser = userRepo.findById(userid);
        Optional<Server> maybeServer = serverRepo.findById(serverid);
        if (maybeServer.isPresent() && maybeUser.isPresent())
        {
            Server server = maybeServer.get();
            User user = maybeUser.get();
            response = manageConnection(user, server);

        } else
        {
            response = new ResponseEntity<>(maybeUser.orElse(UNKNOWN_USER), HttpStatus.NOT_FOUND);

        }
        return response;
    }

    @DeleteMapping("/users/{userid}/servers/{serverid}")
    public ResponseEntity<User> handleRemoveUserFromServer(
            @PathVariable(value = "userid", required = true) String userid,
            @PathVariable(value = "serverid", required = true) String serverid)
    {
        final ResponseEntity<User> response;
        Optional<User> maybeUser = userRepo.findById(userid);
        Optional<Server> maybeServer = serverRepo.findById(serverid);
        LOGGER.error("TRYING TO DELETE SESSION");
        if (maybeServer.isPresent() && maybeUser.isPresent())
        {
            Server server = maybeServer.get();
            User user = maybeUser.get();
            ActiveConnectionManager connectionManager = ActiveConnectionManager.getInstance();
            ServerConnection activeConnection = connectionManager.getConnection(user.getName());
            if (activeConnection != null)
            {
                LOGGER.error("ABOUT TO DELETE SESSION FOR "+ user.getName());
                activeConnection.close(DisconnectReason.Intentional_Disconnect);
                response = new ResponseEntity<>(user, HttpStatus.OK);
            } else
            {
                response = new ResponseEntity<>(user, HttpStatus.NOT_FOUND);
            }

        } else
        {
            response = new ResponseEntity<>(maybeUser.orElse(UNKNOWN_USER), HttpStatus.NOT_FOUND);
        }
        return response;
    }

    @GetMapping("/servers/{serverid}/users/{userid}")
    public String handleGetUserInServer(
            @PathVariable(value = "userid", required = true) String userid,
            @PathVariable(value = "serverid", required = true) String serverid)
    {
        ActiveConnectionManager connectionManager = ActiveConnectionManager.getInstance();

        return "OK";
    }

    public ResponseEntity<User> manageConnection(User user, Server server)
    {
        ResponseEntity<User> response;
        ActiveConnectionManager connectionManager = ActiveConnectionManager.getInstance();
        ServerConnection activeConnection = connectionManager.getConnection(user.getName());
        if (activeConnection == null)
        {
            ServerConnection connection = startServerConnection(user, server);
            LOGGER.error("USER CONNECTION CREATED UNDER " + connection.getPlayerName());
            connectionManager.initConnection(connection, false, null);
            connection.getEventHandler().registerListener(new ChatListener(LOGGER));
            connection.getEventHandler().registerListener(new PlayerStateListener(LOGGER));
            user.setServer(server.getName());
            response = new ResponseEntity<>(user, HttpStatus.OK);
        } else
        {
            response = new ResponseEntity<>(user, HttpStatus.NOT_FOUND);
        }
        return response;
    }

    public ServerConnection startServerConnection(User user, Server server)
    {
        final ServerConnection response;
        AuthenticationHandler auth;
        auth = sessionManager.getAccount(user.getEmail().toLowerCase());
        if (auth == null)
        {
            auth = sessionManager.authNewAccount(user.getEmail(), user.getPassword());
        }

        if (auth != null)
        {
            response = new ServerConnection(server.getAddress(), Integer.parseInt(server.getPort()), LOGGER, auth);
        } else
        {
            response = null;
        }
        return response;

    }

}
