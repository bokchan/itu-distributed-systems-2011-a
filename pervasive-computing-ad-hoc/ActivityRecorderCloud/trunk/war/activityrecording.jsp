<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreService" %>
<%@ page import="com.google.appengine.api.datastore.Query" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="com.google.appengine.api.datastore.FetchOptions" %>
<%@ page import="com.google.appengine.api.datastore.Key" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>

<html>
  <head>
    <!-- <link type="text/css" rel="stylesheet" href="/stylesheets/main.css" /> -->
  </head>

  <body>

<%
    String activityName = request.getParameter("activityName");
    if (activityName == null) {
        activityName = "default";
    }
    // if (activityType == null) {
    //     activityType = "default";
    // }

%>

<!-- list old / already created messages -->

<%
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Key activityKey = KeyFactory.createKey("Activity", activityName);
    
    // Run an ancestor query to ensure we see the most up-to-date
    // view of the Greetings belonging to the selected Guestbook.
    // Query query = new Query("ActivityNode", activityKey).addSort("date", Query.SortDirection.DESCENDING);
    Query query = new Query("ActivityNode", activityKey).addSort("time", Query.SortDirection.ASCENDING);
    // List<Entity> result = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(5));
    
    List<Entity> result = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());    
    
    if (result.isEmpty()) {
        %>
        <h1>Activity '<%= activityName %>' has no nodes.</h1>
        <%
    } else {
        %>
        <h1>Nodes in '<%= activityName %>' motion.</h1>
        
        <table>
            <thead>
            <tr>
                <th>user name</th>
                <th>type</th>                
                <th>time</th>
                <th>x</th>                
                <th>y</th>                
                <th>z</th>                
            </tr>
            </thead>        
            <tbody>
        <%
        for (Entity item : result) {
            %>

            <tr>
                <td><%= item.getProperty("username") %></td>
                <td><%= item.getProperty("type") %></td>                
                <td><%= item.getProperty("time") %></td>
                <td><%= item.getProperty("x") %></td>                
                <td><%= item.getProperty("y") %></td>                
                <td><%= item.getProperty("z") %></td>                
            </tr>
            
            <%
        }
        

        
    }
%>
            </tbody>
        </table>
    <!-- write a new message -->



    <fieldset>
        <legend>add activity node</legend>

    <form action="/add_recording" method="post">

      <div>
          <label>Activity Type</label>
          <select name="activityType">
              <option value="Running">Running</option>
              <option value="Walking">Walking</option>
              <option value="Stairs">Stairs</option>
              <option value="None">None</option>
          </select>
      </div>
      
      <div><label>x</label><input type="text" name="activityX" size="6" /></div>
      <div><label>y</label><input type="text" name="activityY" size="6" /></div>
      <div><label>z</label><input type="text" name="activityZ" size="6" /></div>      
      <div><label>time <!--(unix time sample)--></label><input type="text" name="activityTime" size="8" /></div>      
      <div><label>username</label><input type="text" name="activityUserName" size="20" default="ttw" /></div>            
      
      
      <div>
          <input type="submit" value="Post Activity Node" />
          <input type="hidden" name="activityName" value="<%= activityName %>"/>
      </div>
      
    </form>
</fieldset>
  </body>
</html>