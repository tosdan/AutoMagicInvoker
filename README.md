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

Ipotizziamo di avere una webapp in esecuzione all'URL 
~~~
http://host.it/webapp
~~~ 
e di aver configurato nel web.xml il prametro `CLASS_PATH` con il package 
~~~
com.github.tosdan.autominvk.apps
~~~
 
Per eseguire l'azione __sonoUnaAzioneInvocabile__ della classe com.github.tosdan.autominvk.apps.__DemoAmAction__ basterà effettuare una chiamata HTTP all'URL 
~~~
http://host.it/webapp/demo.sonoUnaAzioneInvocabile
~~~

Nella chiamata HTTP il nome della classe dovrà essere scritto in __camelCase__, come nell'esempio. Inoltre il suffisso *__AmAction__* non deve essere specificato. Non è obbligatorio che il nome delle classi *Controller* termini con il suffisso *AmAction*. Il nome della classe scritto in questo modo è solo una convenzione del framework per rendere più semplice distinguerle dalle normali classi. Al momento della creazione dell'indice delle classi *Controller* il suffisso *AmAction* viene ignorato. Se avessimo provato ad eseguire la seguente chiamata 
~~~
http://host.it/webapp/demoAmAction.sonoUnaAzioneInvocabile
~~~ 
il framework avrebbe restituito un errore perchè l'azione [demoAmAction.sonoUnaAzioneInvocabile] non è presente nell'indice delle azioni disponibili. 

#### Sub-package

A partire dal package principale, specificato col parametro `CLASS_PATH` è possibile creare una gerarchia di sotto package per ordinare i vari *Controller*.
L'URL delle chiamate HTTP dovrà essere composto di conseguenza, aggiungendo all'URL di base (url webapp + url-pattern), i sotto package necessari per raggiungere la classe *Controller* desiderata.

Quindi se la classe *Controller* fosse situata nel sotto package __demoApp__ 
~~~
com.github.tosdan.autominvk.apps.demoApp
~~~
la chiamata HTTP dovrebbe essere inoltrata all'url 
~~~
http://host.it/webapp/demoApp/demo.sonoUnaAzioneInvocabile
~~~


### Parametri della chiamata HTTP

Per leggere, dal *Controller*, i parametri di una chiamata HTTP ci sono due possibilità.

 * Leggere i parametri direttamente dall'oggetto *HttpServletRequest*
 * Costruire un oggetto che verrà popolato automatiticamente (approccio raccomandato)

#### Oggetto HttpServletRequest

Per accedere all'oggetto __HttpServletRequest__ è sufficiente creare un campo nella classe controller che sia di tipo HttpServletRequest.

~~~java
package com.github.tosdan.autominvk.apps;

@IamInvokable
public class DemoAmAction {
	
	// Oggetto request popolato dal framework
	private HttpServletRequest req;	

	@IamInvokableAction
	public Object sonoUnaAzioneInvocabile() {
		// recupero del parametro1 dall'oggetto request
		String parametro1 = req.getParameter("parametro1");
		...
	}
}
~~~

Il campo __req__ è di tipo *HttpServletRequest* e il framework automaticamente assegnerà a questo campo l'oggetto rappresentante la request HTTP corrente. A questo punto basterà richiamarlo nel codice del metodo per accedere ai parametri. 

#### Oggetto "parametro" popolato automaticamente

Nell'esempio che segue, viene definita una classe *interna*, o classe *annidata*, che rappresenta i parametri che riceveremo nella chiamata HTTP (la classe può benissimo essere definita anche in maniera tradizionale, sempre di una comune classe si tratta). Il framework individua che il metodo __sonoUnaAzioneInvocabile__ accetta un parametro, quindi individua la classe di questo parametro, ne crea una istanza e ne popola i campi con i parametri contenuti nella chiamata HTTP. 

~~~java
package com.github.tosdan.autominvk.apps;

@IamInvokable
public class DemoAmAction {

	// Classe dell'oggetto "parametro" 
	public static class MyDemoParamsObject {
		// I nomi di questi campi corrispondono ai nomi dei parametri 
		// contenuti nella chiamata HTTP.
		private String param1;
		private boolean param2;
		// Range è un semplice oggetto con campi min e max
		private Range range; 
		public String getParam1() {
			return this.param1;
		}
		public void setParam1(String value) {
			this.param1 = value;
		}
		public boolean isParam2() {
			return this.param2;
		}
		public void setParam2(boolean value) {
			this.param2 = value;
		}
		public boolean getRange() {
			return this.range;
		}
		public void setRange(Range value) {
			this.arnge= value;
		}
	}

	@IamInvokableAction
	public Object sonoUnaAzioneInvocabile(MyDemoParamsObject params) {
		// recupero dei parametri dall'oggetto MyDemoParamsObject
		String parametro1 = params.getParam1();
		boolean parametro2 = params.isParam2();
		
		Range range = params.getRange();
		int min = range.getMin();
		int max = range.getMax();
		...
	}
}
~~~

Vengono popolati solo quei campi il cui nome corrisponde ad un parametro presente nella chiamata HTTP. Un esempio di parametri validi contenuti nella chiamata HTTP potrebbero essere i seguenti:
~~~json
{
	"param1": "valoreA",
	"param2": "valoreB",
	"range": {
		"min": 0,
		"max": 10
	}
}
~~~ 

In pratica il json della chiamata HTTP viene deserializzato in un oggetto Java.
__NB.__ 
In caso di chiamata di tipo POST e PUT i parametri vengono cercati nel corpo della chiamata.
Mentre nel caso di una chiamata di tipo GET o DELETE, i parametri vengono cercati nella querystring.
Mai i parametri vengono cercati in entrambi, body e querystring.

### Parametri del contesto della webapp e della sessione

Similmente a quanto visto per l'oggetto *HttpRequestBeanBuilder* è possibile accedere anche al contesto dell'applicazione, ServletContext, e alla sessione corrente, HttpSession.

~~~java
package com.github.tosdan.autominvk.apps;

@IamInvokable
public class DemoAmAction {
	
	private ServletContext context; // popolato dal framework	
	private HttpSession session;	// popolato dal framework

	@IamInvokableAction
	public Object sonoUnaAzioneInvocabile() {
		// recupero del parametro1 dalla sessione
		String sessionParam1 = (String) session.getAttribute("parametro1");
		
		// recuper del parametroA dal contesto della webapp
		String contextParamA = context.getInitParameter("parametroA");
		...
	}
}
~~~

### Response

Per inoltrare una risposta alla chiamata HTTP è sufficiente che il metodo `IamInvokableAction` invocato restituisca un oggetto.









