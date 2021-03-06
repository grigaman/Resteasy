package org.jboss.resteasy.test.undertow;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;

import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class AsyncIOTest
{

   static Client client;
   static UndertowJaxrsServer server;

   @ApplicationPath("/")
   public static class MyApp extends Application
   {
      @Override
      public Set<Class<?>> getClasses()
      {
         HashSet<Class<?>> classes = new HashSet<Class<?>>();
         classes.add(AsyncIOResource.class);
         classes.add(AsyncWriter.class);
         classes.add(BlockingWriter.class);
         return classes;
      }
   }

   @BeforeClass
   public static void init() throws Exception
   {
      server = new UndertowJaxrsServer().start();
      server.deploy(MyApp.class);
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void stop() throws Exception
   {
       try
       {
          client.close();
       }
       catch (Exception e)
       {

       }
      server.stop();
   }


   @Test
   public void testAsyncIo() throws Exception
   {
      WebTarget target = client.target(generateURL("/async-io/blocking-writer-on-worker-thread"));
      String val = target.request().get(String.class);
      Assert.assertEquals("OK", val);

      target = client.target(generateURL("/async-io/async-writer-on-worker-thread"));
      val = target.request().get(String.class);
      Assert.assertEquals("OK", val);

      target = client.target(generateURL("/async-io/slow-async-writer-on-worker-thread"));
      val = target.request().get(String.class);
      Assert.assertEquals("OK", val);
   }
}