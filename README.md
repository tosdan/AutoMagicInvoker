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

L'`url-pattern` proposto per la servlet è puramente di esempio, non c'è alcun vincolo da rispettare. 

L'*init-param* `CLASS_PATH` rappresenta il percorso in cui *autominvk* cercherà i *Controller* dell'applicazione, questo parametro è obbligatorio.

__NB.__ Per questione di performance conviene scegliere un package ben specifico. Più classi sono contenute nel package indicato e nei suoi, eventuali, sottopackage più tempo richiede la scansione (normalmente nell'ordine di alcuni ms). Nulla però vieta di impostare un package più generico.

## Panoramica

### Controller

Le classi con *Annotation* `IamInvokable` costituiscono i *Controller* dell'applicazione.
~~~java
package com.github.tosdan.autominvk.apps;
@IamInvokable
public class DemoAmAction {
	...
}
~~~

Per definire un'azione eseguibile via chiamata HTTP, basta apporre l'*Annotation* `IamInvokableAction` ad un metodo della classe *Controller* creata in precedenza.
~~~java
package com.github.tosdan.autominvk.apps;
@IamInvokable
public class DemoAmAction {	

	@IamInvokableAction
	public Object sonoUnaAzioneInvocabile() {
		...
	}
}
~~~

#### Eseguire un'azione di un Controller

Ipotizziamo di avere una webapp in esecuzione all'URL ~~~
http://host.it/webapp
~~~ e di aver configurato nel web.xml il prametro `CLASS_PATH` con il package ~~~
com.github.tosdan.autominvk.apps
~~~
 
Per eseguire l'azione __sonoUnaAzioneInvocabile__ della classe __com.github.tosdan.autominvk.apps.DemoAmAction__ basterà effettuare una chiamata HTTP all'URL ~~~
http://host.it/webapp/demo.sonoUnaAzioneInvocabile
~~~

Nella chiamata HTTP il nome della classe dovrà essere scritto in __camelCase__, come nell'esempio. Inoltre il suffisso *__AmAction__* non deve essere specificato. Non è obbligatorio che il nome delle classi *Controller* termini con *AmAction*, il nome della classe scritto in questo modo è solo una convenzione del framework per rendere semplice distinguerle dalle normali classi. Al momento della creazione dell'indice delle classi *Controller* il suffisso *AmAction* viene ignorato. Se avessimo provato ad eseguire la seguente chiamata ~~~
http://host.it/webapp/demo.sonoUnaAzioneInvocabileAmAction
~~~ 
il framework avrebbe restituito un errore perchè l'azione [demo.sonoUnaAzioneInvocabileAmAction] non è presente nell'indice delle azioni disponibili. 

#### Sub-package

A partire dal package principale (CLASS_PATH) è possibile creare una gerarchia di sotto package. Nell'url da chiamare ogni sotto package si traduce in un sotto path da interporre prima del nome della classe.
Nel caso la classe fosse posizionata in un sottopackage per esempio 
~~~
com.github.tosdan.autominvk.apps.demoApp
~~~
 l'url da chiamare diventerebbe 
~~~
http://host.it/webapp/demoApp/demo.get
~~~


## Usage

