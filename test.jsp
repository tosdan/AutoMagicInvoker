<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Demo Page</title>
</head>
<body>
<h2>Demo Page</h2>
<p><a href="action/demo/hello.forward~json?cippa=lippa">Spatcher</a></p>

<p><a href="action/demo/hello.echo~json?cippa=lippa&amici=miei">Params Echo</a></p>

<p><a href="action/demo/hello.get~json?cippa=lippa">Get</a></p>

<p><a href="action/demo/helloWorld.get?greet=Ciao&name=World&year=2015&hours=2.5&hello=circolare">Get HelloWorld</a></p>

<fieldset>
	<form action="action/demo/helloWorld.post" method="post">
		Greet: <input type="text" value="Ciao" name="greet"><br>
		Name: <input type="text" value="Mondo" name="name"><br>
		Year: <input type="text" value="2015" name="year"><br>
		Hours: <input type="text" value="2,5" name="hours"><br>
		<br>
		Multiplo: <input name="multiplo" type="text" value="ciao">
		Multiplo: <input name="multiplo" type="text" value="hello">
		Multiplo: <input name="multiplo" type="text" value="bye">
		<br>
		Multiplo2: <input name="multiplo2" type="text" value="boh">
		Multiplo2: <input name="multiplo2" type="text" value="beh">
		Multiplo2: <input name="multiplo2" type="text" value="bah">
		<br>
		<br>
		<button>Invia</button>
	</form>
</fieldset>

</body>
</html>