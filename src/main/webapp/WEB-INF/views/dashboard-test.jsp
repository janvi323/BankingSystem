<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Test Dashboard</title>
</head>
<body>
    <h1>Dashboard Test</h1>
    <p>Username: ${username}</p>
    <p>User Role: ${userRole}</p>
    <p>User ID: ${userId}</p>
    
    <script>
        console.log('Dashboard test loaded successfully');
    </script>
</body>
</html>