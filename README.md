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
e url-pattern
~~~
/api/*
~~~
 
Per eseguire l'azione __sonoUnaAzioneInvocabile__ della classe com.github.tosdan.autominvk.apps.__DemoAmAction__ basterà effettuare una chiamata HTTP all'URL 
~~~
http://host.it/webapp/api/demo.sonoUnaAzioneInvocabile
~~~

Nella chiamata HTTP il nome della classe dovrà essere scritto in __camelCase__, come nell'esempio. Inoltre il suffisso *__AmAction__* non deve essere specificato. Non è obbligatorio che il nome delle classi *Controller* termini con il suffisso *AmAction*. Il nome della classe scritto in questo modo è solo una convenzione del framework per rendere più semplice distinguerle dalle normali classi. Al momento della creazione dell'indice delle classi *Controller* il suffisso *AmAction* viene ignorato. Se avessimo provato ad eseguire la seguente chiamata 
~~~
http://host.it/webapp/api/demoAmAction.sonoUnaAzioneInvocabile
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
http://host.it/webapp/api/demoApp/demo.sonoUnaAzioneInvocabile
~~~


### Parametri della chiamata HTTP

Per leggere, dal *Controller*, i parametri di una chiamata HTTP ci sono due possibilità.

 * Leggere i parametri direttamente dall'oggetto *HttpServletRequest*
 * Costruire un oggetto "parametro" che verrà popolato automatiticamente (approccio raccomandato)

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

#### Oggetto "parametro" popolato automaticamente parte 1

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
		private Date millenniumBugDate;
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
		...
	}

	@IamInvokableAction
	public Object sonoUnaAzioneInvocabile(MyDemoParamsObject params) {
		// recupero dei parametri dall'oggetto MyDemoParamsObject
		String parametro1 = params.getParam1();
		boolean parametro2 = params.isParam2();
		
		Range range = params.getRange();
		int min = range.getMin();
		int max = range.getMax();
		
		Date apocalisse = params.getMillenniumBugDate();
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
	},
	"millenniumBugDate": "31/12/1999"
}
~~~ 

In pratica il json della chiamata HTTP viene deserializzato in un oggetto Java.

__NB.__ 
In caso di chiamata di tipo POST e PUT i parametri vengono cercati nel corpo della chiamata.
Mentre nel caso di una chiamata di tipo GET o DELETE, i parametri vengono cercati nella querystring.
I parametri non vengono mai vengono cercati in entrambi, body e querystring.

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

Per inoltrare una risposta alla chiamata HTTP è sufficiente che il metodo `IamInvokableAction` invocato restituisca un oggetto o anche un tipo primitivo.

~~~java
package com.github.tosdan.autominvk.apps;

@IamInvokable
public class DemoAmAction {

	@IamInvokableAction
	public Object sonoUnaAzioneInvocabile() {
		...
		return "ok";
	}
}
~~~

Di default viene inviata una response con *ContentType* __text/html__ e l'oggetto restituito viene convertito in stringa con il metodo toString(), mentre nel caso di un primitivo viene restituito così com'è nel contenuto della response.

Pur essendo un approccio valido non offre molto margine per inviare una response particolarmente elaborata.

#### Response Render

Un approccio migliore è quello di restituire un oggetto invece di un primitivo e di impostare una *strategia di renderizzazione* per la response.

~~~java
package com.github.tosdan.autominvk.apps;

// import della classe per il render Json. La classe Json implementa AutoMagicRender.
import com.github.tosdan.autominvk.rendering.render.Json;

@IamInvokable
public class DemoAmAction {

	// Configurazione dell'elemento render impostando il valore Json.class.	
	@IamInvokableAction(render=Json.class)
	public Object sonoUnaAzioneInvocabile() {
		...
		// un ipotetico oggetto da restituire in risposta
		Map returnValue = new HashMap();
		
		// popolamento dei dati per la risposta
		returnValue.put("message", "ok");
		returnValue.put("risposta", 42);
		
		List valori = new ArrayList();
		valori.add("abc");
		valori.add("XYZ");
		valori.add("007");
		returnValue.put("valori", valori);
		
		return returnValue;
	}
}
~~~

Per configurare una *strategia di renderizzazione* basta aggiungere l'*elemento* `render` nell'*Annotation* `IamInvokableAction` e fornire come valore una classe che implementi l'interfaccia `AutoMagicRender`.

La response inviata avrebbe la forma:
~~~json
{
	"message": "ok",
	"risposta": 42,
	"valori": [
		"abc",
		"XYZ",
		"007"
	]
}
~~~ 

Di default il render Json imposta il *ContentType* con __text/plain__ per retrocompatibilità con Internet Explorer (altrimenti la versione 8 ad esempio cercherebbe di effettuare un download) 

Un'altra particolarità del render Json è che nel caso in cui l'esecuzione del metodo invocato, per esempio sonoUnaAzioneInvocabile(), risulti in una eccezione, verrà restituito un oggetto json simile al seguente:
~~~json
{
	"error": "Messaggio di errore ottenuto da exception.getMessage()",
	"stacktrace": "Righe dello \n stacktrace ottenute \n dall'eccezione lanciata."
}
~~~ 

Quindi, gestendo opportunamente il sistema di eccezioni, si può sfruttare il *messagge* dell'eccezione per fornire all'utente un messaggio che descriva il problema avvenuto.

#### Custom ContentType

Per impostare un *ContentType* differente da quello di default del `AutoMagicRender` utilizzato, basta specificare l'*elemento* `mime` nell'*Annotation* `IamInvokableAction`.

~~~java
package com.github.tosdan.autominvk.apps;
import com.github.tosdan.autominvk.rendering.render.Json;

@IamInvokable
public class DemoAmAction {

	@IamInvokableAction(render=Json.class, mime="application/json")
	public Object sonoUnaAzioneInvocabile() {
		...
		Map returnValue = new HashMap();
		...
		return returnValue;
	}
}
~~~

#### Oggetto "parametro" popolato automaticamente parte 2 - Deserializzazione di date

Per consentire al sistema che deserializza i parametri della chiamata HTTP in un oggetto Java di interpretare correttamente le date, il formato da usare è quello italiano __GG/MM/AAAA__. Nell'esempio della parte 1 di questa sezione infatti era stata passata la data *31/12/1999*. 

&Egrave; possibile comunque impostare un proprio formato e sovrascrivere quello di default aggiungendo l'*elemento* `gsonDateFormat` nell'*Annotation* `IamInvokableAction` .
Per definire il proprio formato fare riferimento ai pattern di [SimpleDateFormat] (http://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html) 
~~~java
package com.github.tosdan.autominvk.apps;

@IamInvokable
public class DemoAmAction {

	public static class MyDemoParamsObject {
		private Date millenniumBugDate;
		public String getMillenniumBugDate() {
			return this.millenniumBugDate;
		}
		public void setMillenniumBugDate(Date value) {
			this.millenniumBugDate = value;
		}		
		...
	}

	// l'elemento gsonDateFormat consente di impostare il proprio formato per la deserializzazione delle date
	@IamInvokableAction(render=Json.class, gsonDateFormat="yyyy-MM-dd")
	public Object sonoUnaAzioneInvocabile(MyDemoParamsObject params) {
		Date apocalisse = params.getMillenniumBugDate();
		...
	}
}
~~~
Parametri della chiamata:
~~~json
{
	"millenniumBugDate": "1999-12-31"
}
~~~ 

### Metodi HTTP

Impostando l'*elemento* `reqMethod` nell'*Annotation* `IamInvokableAction` è possibile limitare l'accessibilità del metodo da invoare in base al metodo HTTP utilizzato:
~~~java
package com.github.tosdan.autominvk.apps;

@IamInvokable
public class DemoAmAction {
	@IamInvokableAction(reqMethod="POST")
	public Object sonoUnaAzioneInvocabile(MyDemoParamsObject params) {
		...
	}
}
~~~ 
Con questa configurazione il metodo sonoUnaAzioneInvocabile() verrà invocato solo se la chiamata HTTP è di tipo POST. In caso di altri metodi HTTP (GET, PUT, HEAD o DELETE) verrà restituito un errore.

### URL Alias

&Egrave; possibile assegnare un *alias* al *Controller* che contiene il metodo da invocare, in modo che sia diverso da quello effettivo, utilizzando l'*elemento* `alias` nell'*Annotation* `IamInvokableAction`

~~~java
package com.github.tosdan.autominvk.apps;

@IamInvokable
public class DemoAmAction {
	@IamInvokableAction(alias="Dimostrazione")
	public Object sonoUnaAzioneInvocabile(MyDemoParamsObject params) {
		...
	}
}
~~~ 
A questo punto la chiamata dovrà essere:
~~~
<!-- L'alias va riportato così com'è stato scritto, quindi con l'iniziale maiuscola -->
http://host.it/webapp/api/Dimostrazione.sonoUnaAzioneInvocabile
~~~
Notare che l'iniziale minuscola è solo una convenzione e come tale, nel momento in cui viene definito un *alias*, ogni convenzione viene meno.

Nel caso l'*alias* inizi con il carattere slash `/`, l'URL per richiamare il *Controller* dovrà essere la parte dell'`url-pattern` più l'*alias* stesso.
~~~java
package com.github.tosdan.autominvk.apps.miaApp.controller;

@IamInvokable
public class DemoAmAction {
	@IamInvokableAction(alias="/AbsoluteDemo")
	public Object sonoUnaAzioneInvocabile(MyDemoParamsObject params) {
		...
	}
}
~~~ 
A questo punto la chiamata dovrà essere:
~~~html
<!-- invece di http://host.it/webapp/api/miaApp/controller/AbsoluteDemo.sonoUnaAzioneInvocabile -->
http://host.it/webapp/api/AbsoluteDemo.sonoUnaAzioneInvocabile
~~~


















