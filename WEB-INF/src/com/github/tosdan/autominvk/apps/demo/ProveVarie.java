/**
 * 
 */
package com.github.tosdan.autominvk.apps.demo;

import com.github.tosdan.autominvk.AutoMagicAction;
import com.github.tosdan.autominvk.AutoMagicClassCrawler;
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
		AutoMagicClassCrawler crawler = new AutoMagicClassCrawler("it.blutec.apps");
		AutoMagicMethodInvoker invoker = new AutoMagicMethodInvoker(crawler);
		AutoMagicAction amAction = new AutoMagicAction("/demo/HelloAutoMaricInvoker.get", "");
		Object intance = invoker.invoke(amAction);
		System.out.println("get:"+intance);
	}
}
