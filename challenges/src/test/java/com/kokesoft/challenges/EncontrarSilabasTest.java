package com.kokesoft.challenges;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;

import org.junit.Test;

public class EncontrarSilabasTest {

	@Test
	public void monosilabosTest() {
		//System.out.println(new String(EncontrarSilabas.silabea("él").get(0).getBytes(StandardCharsets.UTF_8)));
		assertEquals(Arrays.asList("él"), EncontrarSilabas.silabea("él"));
		assertEquals(Arrays.asList("la"), EncontrarSilabas.silabea("la"));
		assertEquals(Arrays.asList("sol"), EncontrarSilabas.silabea("sol"));
		assertEquals(Arrays.asList("sí"), EncontrarSilabas.silabea("sí"));
	}

	@Test
	public void bisilabosTest() {
		//System.out.println(new String(EncontrarSilabas.silabea("él").get(0).getBytes(StandardCharsets.UTF_8)));
		assertEquals(Arrays.asList("lu","na"), EncontrarSilabas.silabea("luna"));
		assertEquals(Arrays.asList("puer","ta"), EncontrarSilabas.silabea("puerta"));
		assertEquals(Arrays.asList("la","do"), EncontrarSilabas.silabea("lado"));
		assertEquals(Arrays.asList("ca","mión"), EncontrarSilabas.silabea("camión"));
		assertEquals(Arrays.asList("tron","co"), EncontrarSilabas.silabea("tronco"));
		assertEquals(Arrays.asList("co","che"), EncontrarSilabas.silabea("coche"));
		assertEquals(Arrays.asList("cla","se"), EncontrarSilabas.silabea("clase"));
		assertEquals(Arrays.asList("co","pla"), EncontrarSilabas.silabea("copla"));
		assertEquals(Arrays.asList("so","plar"), EncontrarSilabas.silabea("soplar"));
		assertEquals(Arrays.asList("ca","ble"), EncontrarSilabas.silabea("cable"));
		assertEquals(Arrays.asList("ta","bla"), EncontrarSilabas.silabea("tabla"));
		assertEquals(Arrays.asList("plan","ta"), EncontrarSilabas.silabea("planta"));
		assertEquals(Arrays.asList("ti","cket"), EncontrarSilabas.silabea("ticket"));
		assertEquals(Arrays.asList("plie","go"), EncontrarSilabas.silabea("pliego"));
		assertEquals(Arrays.asList("fue","lle"), EncontrarSilabas.silabea("fuelle"));
	}

	@Test
	public void multisilabosTest() {
		//System.out.println(new String(EncontrarSilabas.silabea("él").get(0).getBytes(StandardCharsets.UTF_8)));
		assertEquals(Arrays.asList("la","de","a","do"), EncontrarSilabas.silabea("ladeado"));
		assertEquals(Arrays.asList("suél","ta","lo"), EncontrarSilabas.silabea("suéltalo"));
		assertEquals(Arrays.asList("blo","que","a","do"), EncontrarSilabas.silabea("bloqueado"));
		assertEquals(Arrays.asList("prio","ri","dad"), EncontrarSilabas.silabea("prioridad"));
		assertEquals(Arrays.asList("an","ti","guo"), EncontrarSilabas.silabea("antiguo"));
		assertEquals(Arrays.asList("gue","par","do"), EncontrarSilabas.silabea("guepardo"));
		assertEquals(Arrays.asList("ca","cha", "rro"), EncontrarSilabas.silabea("cacharro"));
		assertEquals(Arrays.asList("do","blan","do"), EncontrarSilabas.silabea("doblando"));
		assertEquals(Arrays.asList("gas","tan","do"), EncontrarSilabas.silabea("gastando"));
		assertEquals(Arrays.asList("far","fu","llan","do"), EncontrarSilabas.silabea("farfullando"));
		assertEquals(Arrays.asList("a","pre","ciar"), EncontrarSilabas.silabea("apreciar"));
		assertEquals(Arrays.asList("a","rran","car"), EncontrarSilabas.silabea("arrancar"));
		assertEquals(Arrays.asList("sor","te","ar"), EncontrarSilabas.silabea("sortear"));
		assertEquals(Arrays.asList("re","plie","gue"), EncontrarSilabas.silabea("repliegue"));
		assertEquals(Arrays.asList("sos","la","yo"), EncontrarSilabas.silabea("soslayo"));
		assertEquals(Arrays.asList("plié","ga","lo"), EncontrarSilabas.silabea("pliégalo"));
	}

	@Test
	public void hiatosTest() {
		//System.out.println(new String(EncontrarSilabas.silabea("él").get(0).getBytes(StandardCharsets.UTF_8)));
		assertEquals(Arrays.asList("tí","o"), EncontrarSilabas.silabea("tío"));
		assertEquals(Arrays.asList("pun","tú","a"), EncontrarSilabas.silabea("puntúa"));
		assertEquals(Arrays.asList("pú","a"), EncontrarSilabas.silabea("púa"));
		assertEquals(Arrays.asList("bú","ho"), EncontrarSilabas.silabea("búho"));
		assertEquals(Arrays.asList("o","í","a"), EncontrarSilabas.silabea("oía"));
	}

	@Test
	public void casosRarosTest() {
		//System.out.println(new String(EncontrarSilabas.silabea("él").get(0).getBytes(StandardCharsets.UTF_8)));
		assertEquals(Arrays.asList("an","ti","güe","dad"), EncontrarSilabas.silabea("antigüedad"));
		assertEquals(Arrays.asList("al","co","hol"), EncontrarSilabas.silabea("alcohol"));
		assertEquals(Arrays.asList("ahue","va","do"), EncontrarSilabas.silabea("ahuevado"));
		assertEquals(Arrays.asList("a","hon","dar"), EncontrarSilabas.silabea("ahondar"));
	}
	
	@Test
	public void mantenerMayusculasYSimbolosTest() {
		//System.out.println(new String(EncontrarSilabas.silabea("él").get(0).getBytes(StandardCharsets.UTF_8)));
		assertEquals(Arrays.asList("An","ti","güe","dad"), EncontrarSilabas.silabea("Antigüedad"));
		assertEquals(Arrays.asList("Al","co","hol"), EncontrarSilabas.silabea("Alcohol"));
		assertEquals(Arrays.asList("Eh,"), EncontrarSilabas.silabea("Eh,"));
	}
	
	@Test
	public void karaokeTest() {
		assertEquals("Pro/ban/do, pro/ban/do", EncontrarSilabas.karaoke("Probando, probando"));
		assertEquals("Pro/ban/do, pro/ban/do\n", EncontrarSilabas.karaoke("Probando, probando\n"));
		assertEquals("Pro/ban/do,\npro/ban/do", EncontrarSilabas.karaoke("Probando,\nprobando"));
		assertEquals("Pro/ban/do,\npro/ban/do\n", EncontrarSilabas.karaoke("Probando,\nprobando\n"));
		try {
			//System.out.println(new String(EncontrarSilabas.karaoke(Files.readString(new File("./zapatillas.txt").toPath(), StandardCharsets.UTF_8)).getBytes(StandardCharsets.UTF_8)));
			String a = EncontrarSilabas.karaoke(Files.readString(new File("./zapatillas.txt").toPath(), StandardCharsets.UTF_8));
			String b = Files.readString(new File("./zapatillas-test.txt").toPath(), StandardCharsets.UTF_8);
			assertEquals(b, a);
		} catch(IOException e) {
			fail(e.toString());
		}
	}
}
