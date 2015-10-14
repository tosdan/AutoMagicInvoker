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
        <url-pattern>/api/*</url-pattern>
    </servlet-mapping>
~~~

L'`url-pattern` proposto per la servlet � puramente di esempio, non c'� alcun vincolo da rispettare. 

L'*init-param* `CLASS_PATH` rappresenta il percorso in cui *autominvk* cercher� i Controller dell'applicazione.
__NB.__ Conviene scegliere un package ben specifico, semplicemente per questioni di performance. Pi� classi sono contenute nel package indicato e nei suoi, eventuali, sottopackage pi� tempo richiede la scansione (normalmente nell'ordine di alcuni ms). Nulla per� vieta di impostare un package pi� generico.

## Panoramica

### Controller

Le classi con *Annotation* `IamInvokable` costituiscono i *Controller* dell'applicazione.
~~~java
@IamInvokable
public class DemoAmAction {
	...
}
~~~

All'interno di queste classi *Controller* solo i metodi con *Annotation* `IamInvokableAction` costituiscono una azione richiamabile tramite chiamata HTTP.
~~~java
@IamInvokable
public class DemoAmAction {	

	@IamInvokableAction
	public Object get() {
		...
	}
}
~~~

Per eseguire l'azione associata al metodo *__get__* della classe *__DemoAmAction__*, ipotizzando che l'url di base della webapp sia 
~~~
http://host.it/webapp
~~~
e che la classe sia nel package indicato dal init-param CLASS_PATH 
~~~
com.github.tosdan.autominvk.apps
~~~
(e non in un sottopackage), basta effettuare una chiamata HTTP all'url
~~~
http://host.it/webapp/demo.get
~~~

Nella chiamata il nome della classe va scritto in __camelCase__, infatti la lettera maiuscola iniziale � stata scritta in minuscolo. Il suffisso *__AmAction__* � "riservato", infatti, come si pu� vedere dall'esempio dei chiamata, viene automaticamente rimosso, in modo che sia possibile identificare le classi richiamabili a colpo d'occhio, ma l'url della chiamata possa essere pi� "pulito". 

A partire dal package principale (CLASS_PATH) � possibile creare una gerarchia di sotto package. Nell'url da chiamare ogni sotto package si traduce in un sotto path da interporre prima del nome della classe.
Nel caso la classe fosse posizionata in un sottopackage per esempio 
~~~
com.github.tosdan.autominvk.apps.demoApp
~~~
 l'url da chiamare diventerebbe 
~~~
http://host.it/webapp/demoApp/demo.get
~~~


## Usage

