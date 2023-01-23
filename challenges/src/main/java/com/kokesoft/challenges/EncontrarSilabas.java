package com.kokesoft.challenges;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class EncontrarSilabas {
	
	public static boolean esSimbolo(char ch) {
		return "abcdefghijklmnñopqrstuvwxyzçáéíóúü".indexOf(ch)==-1;
	}
	
	public static boolean esSimbolo(String str) {
		for(char ch: str.toCharArray()) {
			if (!esSimbolo(ch)) {
				return false;
			}
		}
		return true;
	}
	
	public static List<String> silabea(String palabra) {
		final String VCLS = "aeiouáéíóúü";
		final String VFRTS = "aeoáéóíú";
		final String VDBLS = "iuü";
		List<String> r = new ArrayList<>();
		StringBuilder silb = new StringBuilder();
		char prevch = 0;
		for(char ch: palabra.toLowerCase().toCharArray()) {
			if (silb.length()==0) {
				silb.append(ch);
				prevch = ch;
				continue;
			}
			if (esSimbolo(ch)) {
				silb.append(ch);
				prevch = ch;
				continue;
			}
			if (VCLS.indexOf(ch)==-1) { // consonante
				if (prevch==ch) { // dos consonantes iguales seguidas: ll, rr
					if (silb.length()>1) { // sill en silla -> si, ll
						r.add(silb.substring(0, silb.length()-1));
						silb.setLength(0);
						silb.append(prevch);
					}
					silb.append(ch);
					prevch = ch; // aunque no hace falta
					continue;
				} else
				if (VCLS.indexOf(prevch)==-1) { // dos consonantes seguidas pero distintas
					if (silb.length()==1
							|| (silb.length()>1 && esSimbolo(silb.substring(0, silb.length()-1)))) { // si sólo hay una consonante
						silb.append(ch);
						prevch = ch;
						continue;
					} else
					if (('c'==prevch && 'h'==ch) // si estamos con la che
							|| (('b'==prevch || 'p'==prevch || 'g'==prevch || 'c'==prevch) && 'l'==ch) // o el grupo bl/pl/gl/cl
							|| ('c'==prevch && 'k'==ch) // o el grupo ck (ticket)
							|| ('p'==prevch && 'r'==ch) // o el grupo pr (apreciar)
							) { 
						r.add(silb.toString().substring(0, silb.length()-1));
						silb.setLength(0);
						silb.append(prevch);
						silb.append(ch);
						prevch = ch;
						continue;
					}
					r.add(silb.toString());
					silb.setLength(0);
					silb.append(ch);
					prevch = ch;
					continue;
				} else { // vocal-consonante
					// no puedo hacer nada. Añado y ya veremos después
					silb.append(ch);
					prevch = ch;
					continue;
				}
			} else { // vocal
				boolean hintercalada = false;
				if ('h'==prevch) { // si lo anterior fue una h compruebo si lo anterior tb fue una vocal
					if (silb.length()>1 && VCLS.indexOf(silb.charAt(silb.length()-2))!=-1) {
						prevch = silb.charAt(silb.length()-2); // hacemos como si la última letra fuera la vocal
						hintercalada = true; // casos ahuevado y ahondar
					}
				}
				if (VCLS.indexOf(prevch)==-1) { // lo anterior fue una consonante
					if (silb.toString().indexOf('a')!=-1 || silb.toString().indexOf('á')!=-1
							|| silb.toString().indexOf('e')!=-1 || silb.toString().indexOf('é')!=-1
							|| silb.toString().indexOf('i')!=-1 || silb.toString().indexOf('í')!=-1
							|| silb.toString().indexOf('o')!=-1 || silb.toString().indexOf('ó')!=-1
							|| silb.toString().indexOf('u')!=-1 || silb.toString().indexOf('ú')!=-1) { // si ya tiene una vocal
						r.add(silb.substring(0, silb.length()-1));
						silb.setLength(0);
						silb.append(prevch);
					}
					silb.append(ch);
					prevch = ch;
					continue;
				} else { // lo anterior fue una vocal
					if(VFRTS.indexOf(ch)!=-1) { // la vocal de ahora es fuerte o débil con tilde
						if (VFRTS.indexOf(prevch)!=-1) { // si la anterior era fuerte o débil con tilde, hiato
							if (hintercalada) {
								r.add(silb.substring(0, silb.length()-1));
								silb.setLength(0);
								silb.append('h');
							} else {
								r.add(silb.toString());
								silb.setLength(0);
							}
						}
					}
					silb.append(ch);
					prevch = ch;
					continue;
				}
			}
		}
		if (silb.length()!=0) {
			r.add(silb.toString());
		}
		if (!String.join("", r).equals(palabra)) {
			List<String> nr = new ArrayList<>();
			int i = 0;
			for(String s: r) {
				nr.add(palabra.substring(i, i+s.length()));
				i += s.length();
			}
			return nr;
		}
		return r;
	}
	
	public static String karaoke(String letra) {
		StringBuilder sb = new StringBuilder();
		try(BufferedReader brd = new BufferedReader(new StringReader(letra))) {
			String line;
			while( (line=brd.readLine())!=null ) {
				boolean first = true;
				for(String palabra: line.trim().split("\\s+")) {
					if (!first) sb.append(' ');
					else first = false;
					List<String> silabas = silabea(palabra);
					sb.append(String.join("/", silabas));
				}
				sb.append('\n');
			}
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
		if (letra.endsWith("\n"))
			return sb.toString();
		else
			return sb.toString().substring(0, sb.length()-1);
	}
}
