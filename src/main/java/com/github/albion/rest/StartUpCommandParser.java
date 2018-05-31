package com.github.albion.rest;

import com.github.maxopoly.angeliacore.SessionManager;
import com.github.maxopoly.angeliacore.connection.ServerConnection;
import com.github.maxopoly.angeliacore.connection.login.AuthenticationHandler;
import java.io.Console;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.Logger;

public class StartUpCommandParser
{

    private static Options options;

    static
    {
        options = new Options();
        options.addOption("user", true, "Username");
        options.addOption("password", true, "Password");
        options.addOption("ip", true, "Server IP");
        options.addOption("port", true, "Server port");
    }

    public static ServerConnection parse(String[] args, Logger logger)
    {
        CommandLineParser parser = new DefaultParser();
        SessionManager sessionManager = new SessionManager(logger, true);
        String userName = System.getProperty("USER");
        String password = System.getProperty("PASSWORD");
        String serverName = System.getProperty("SERVER_NAME");
        String serverPort = System.getProperty("SERVER_PORT");

        Console c = System.console();

        AuthenticationHandler auth;

        auth = sessionManager.getAccount(userName.toLowerCase());
        if (auth == null)
        {

            auth = sessionManager.authNewAccount(userName, password);
            if (auth == null)
            {
                logger.info("Wrong password");
                return null;
            }
        }

        if (serverPort != null && !serverPort.isEmpty())
        {
            try
            {
                int port = Integer.parseInt(serverPort);
                return new ServerConnection(serverName, port, logger, auth);
            } catch (NumberFormatException e)
            {
                logger.error(serverPort + " is not a valid number");
            }
        } else
        {
            return new ServerConnection(serverName, logger, auth);
        }
        return null;

    }
}
