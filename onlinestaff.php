<?php
$con = new mysqli("localhost", "user", "password", "database");

$query = "SELECT * FROM OnlineStaff";

$finalString = "";

if($result = $con->query($query)) {
	while($row = $result->fetch_assoc()) {
		if(!$row['is_online']) {
			$isOnline = "Offline";
		}
		else {
			$isOnline = "Online";
		}
		$finalString .= "<tr><td>".$row['player']."</td><td>".$row['last_online']."</td><td>".$isOnline."</td></tr>";
	}
}

$con->close();

?>

<!DOCTYPE html>
<html>
<head>
	<title>Online Staff</title>
	<style type="text/css">
	table,tr,td {
		border: 1px black solid;
		padding: 5px;
	}
	</style>
</head>

<body>
	<table>
	<tr>
		<th>Player</th>
		<th>Last Online</th>
		<th>Current Status</th>
	</tr>
	<?php echo $finalString; ?>
	</table>
</body>
</html>