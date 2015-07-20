package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;

public final class test_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final JspFactory _jspxFactory = JspFactory.getDefaultFactory();

  private static java.util.List _jspx_dependants;

  private javax.el.ExpressionFactory _el_expressionfactory;
  private org.apache.AnnotationProcessor _jsp_annotationprocessor;

  public Object getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _el_expressionfactory = _jspxFactory.getJspApplicationContext(getServletConfig().getServletContext()).getExpressionFactory();
    _jsp_annotationprocessor = (org.apache.AnnotationProcessor) getServletConfig().getServletContext().getAttribute(org.apache.AnnotationProcessor.class.getName());
  }

  public void _jspDestroy() {
  }

  public void _jspService(HttpServletRequest request, HttpServletResponse response)
        throws java.io.IOException, ServletException {

    PageContext pageContext = null;
    HttpSession session = null;
    ServletContext application = null;
    ServletConfig config = null;
    JspWriter out = null;
    Object page = this;
    JspWriter _jspx_out = null;
    PageContext _jspx_page_context = null;


    try {
      response.setContentType("text/html; charset=UTF-8");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;

      out.write("\n");
      out.write("<!DOCTYPE html>\n");
      out.write("<html>\n");
      out.write("<head>\n");
      out.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n");
      out.write("<title>Demo Page</title>\n");
      out.write("<script type=\"text/javascript\" src=\"jquery-2.1.4.js\"></script>\n");
      out.write("<script type=\"text/javascript\" src=\"script.js\"></script>\n");
      out.write("</head>\n");
      out.write("<body>\n");
      out.write("<h2>Demo Page</h2>\n");
      out.write("<p><a href=\"action/demo/hello.forward~json?cippa=lippa\">Spatcher</a></p>\n");
      out.write("\n");
      out.write("<p><a href=\"action/demo/hello.echo~json?cippa=lippa&amici=miei\">Params Echo</a></p>\n");
      out.write("\n");
      out.write("<p><a href=\"action/demo/hello.get~json?cippa=lippa\">Get</a></p>\n");
      out.write("\n");
      out.write("<p><a href=\"action/demo/helloWorld.get?greet=Ciao&name=World&year=2015&hours=2.5&hello=circolare\">Get HelloWorld</a></p>\n");
      out.write("\n");
      out.write("<p><button type=\"button\" id=\"ajaxPost\">Ajax POST</button></p>\n");
      out.write("<p><button type=\"button\" id=\"ajaxGet\">Ajax GET</button></p>\n");
      out.write("\n");
      out.write("<fieldset>\n");
      out.write("\t<form action=\"action/demo/helloWorld.post\" method=\"post\">\n");
      out.write("\t\tGreet: <input type=\"text\" value=\"Ciao\" name=\"greet\"><br>\n");
      out.write("\t\tName: <input type=\"text\" value=\"Mondo\" name=\"name\"><br>\n");
      out.write("\t\tYear: <input type=\"text\" value=\"2015\" name=\"year\"><br>\n");
      out.write("\t\tHours: <input type=\"text\" value=\"2.5\" name=\"hours\"><br>\n");
      out.write("\t\tBooleano: <input type=\"text\" value=\"True\" name=\"booleano\"><br>\n");
      out.write("\t\t<br>\n");
      out.write("\t\tMultiplo: <input name=\"multiplo\" type=\"text\" value=\"ciao\">\n");
      out.write("\t\tMultiplo: <input name=\"multiplo\" type=\"text\" value=\"hello\">\n");
      out.write("\t\tMultiplo: <input name=\"multiplo\" type=\"text\" value=\"bye\">\n");
      out.write("\t\t<br>\n");
      out.write("\t\tMultiplo2: <input name=\"multiplo2\" type=\"text\" value=\"boh\">\n");
      out.write("\t\tMultiplo2: <input name=\"multiplo2\" type=\"text\" value=\"beh\">\n");
      out.write("\t\tMultiplo2: <input name=\"multiplo2\" type=\"text\" value=\"bah\">\n");
      out.write("\t\t<br>\n");
      out.write("\t\t<br>\n");
      out.write("\t\t<button>Invia</button>\n");
      out.write("\t</form>\n");
      out.write("</fieldset>\n");
      out.write("\n");
      out.write("</body>\n");
      out.write("</html>");
    } catch (Throwable t) {
      if (!(t instanceof SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          try { out.clearBuffer(); } catch (java.io.IOException e) {}
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}
