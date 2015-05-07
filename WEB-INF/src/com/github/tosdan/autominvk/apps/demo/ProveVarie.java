/**
 * 
 */
package com.github.tosdan.autominvk.apps.demo;

import com.github.tosdan.autominvk.AutoMagicAction;
import com.github.tosdan.autominvk.IamIvokableClassCrawler;
import com.github.tosdan.autominvk.AutoMagicMethodInvoker;

/**
 * @author tosdan
 *
 */
public class ProveVarie {

	/**
	 * 
	 */
	public ProveVarie() {
		// TODO Auto-generated constructor stub
	}

	public static void main( String[] args ) throws Exception {
		IamIvokableClassCrawler crawler = new IamIvokableClassCrawler("com.github.tosdan.autominvk.apps");
		AutoMagicMethodInvoker invoker = new AutoMagicMethodInvoker(crawler);
		AutoMagicAction amAction = new AutoMagicAction("/demo/hello.get", "", "get");
		Object intance = invoker.invoke(amAction);
		System.out.println("get:"+intance);
	}
}
