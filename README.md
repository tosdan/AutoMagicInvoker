# AutoMagicInvoker
Micro mvc framework

## Install

Grab the zip fie from the last release and unpack into your project.


Add the following lines in the prject's `web.xml`
~~~xml
	<servlet>
		<description>Servlet da chiamare per l'invocazione automatica delle azioni che implementano IamInvokable</description>
		<servlet-name>AutoMagicInvoker</servlet-name>
		<servlet-class>com.github.tosdan.autominvk.AutoMagicInvokerServlet</servlet-class>
		<init-param>
			<description>Percorso in cui verranno cercate le classi con annotation IamIvokable.
			Ovvero quelle classi per cui e' possibile invocare metodi utilizzando una convenzione negli URL chiamati.
			</description>
			<param-name>CLASS_PATH</param-name>
			<param-value>com.github.tosdan.autominvk.apps</param-value>
		</init-param>
	</servlet>
	<servlet-mapping>
        <servlet-name>AutoMagicInvoker</servlet-name>
        <url-pattern>/action/*</url-pattern>
    </servlet-mapping>
~~~

Set the *CLASS_PATH* param to the folder you want to put your *Action*. Set the *url-pattern* to a desired url.

## Usage

