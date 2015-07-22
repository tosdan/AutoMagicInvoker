# AutoMagicInvoker
Micro mvc framework

## Installazione

Scaricare lo zip dell'ultima release e copiare i jar nel proprio progetto (nella cartella lib sono contenuti jar con le dipendenze). 


Nel `web.xml` aggiungere la seguente servlet:
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

L'`url-pattern` proposto è puramente indicativo, non c'è alcun vincolo da rispettare. L'*ini-param* `CLASS_PATH` indica ad *autominvk* dove cercare le classi `AmAction`.
Conviene specificare un package particolare semplicemente per questioni di performance, nulla di vieta di impostare un package più generico.
Solo le classi con *Annotation* `IamInvokable` vengono censite. Queste classi costituiscono i *Controller*.
~~~java
@IamInvokable
public class Demo {
	...
}
~~~
All'interno di queste classi *Controller* solo i metodi con *Annotation* `IamInvokableAction` costituiscono una azione richiamabile tramite chiamata HTTP.
~~~java
@IamInvokable
public class Demo {	

	@IamInvokableAction
	public Object get() {
		...
	}
}
~~~


## Usage

