package com.kokesoft.concurrency;

import static java.lang.String.format;
import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.junit.Test;

import com.kokesoft.tools.BigList;
import com.kokesoft.tools.CsvReader;

public class CollectionMultithreadTest {
	
	protected int lines(String file) {
		try(BufferedReader br = new BufferedReader(new FileReader(new File(file), StandardCharsets.UTF_8))) {
			int n = 0;
			while(br.readLine()!=null)
				n++;
			return n;
		} catch(IOException e) {
			fail(e.toString());
			return -1;
		}			
	}
	
	protected Map<String,List<List<String>>> readFormattedCsv(Map<String,List<List<String>>> dest, String key, String file, String formato, 
			boolean syncm, boolean synca, int n) {
		return readFormattedCsv(dest, key, file, formato, syncm, synca, n, 0, Integer.MAX_VALUE);
	}
	protected Map<String,List<List<String>>> readFormattedCsv(Map<String,List<List<String>>> dest, String key, String file, 
			String formato, boolean syncm, boolean synca, int n, int line, int nlines) {
		try(BufferedReader br = new BufferedReader(new FileReader(new File(file), StandardCharsets.UTF_8))) {
			CsvReader csvRdr = new CsvReader(br);
			Map<String,String> data;
			int i = 0;
			if (formato!=null)
				csvRdr.setFormato(formato);
			while(line>0 && csvRdr.readFormattedData()!=null)
				line--;
			while(i<nlines && (data=csvRdr.readFormattedData())!=null ) {
				String vkey = data.get(key);
				if (syncm) {
					synchronized (dest) {
						if (!dest.containsKey(vkey)) {
							dest.put(vkey, new ArrayList<>());
						}
						if (synca) {
							List<List<String>> vs = dest.get(vkey);
							synchronized (vs) {
								vs.add(new ArrayList<>(data.values()));
							}
						} else {
							dest.get(vkey).add(new ArrayList<>(data.values()));
						}
					}
				} else {
					if (!dest.containsKey(vkey)) {
						dest.put(vkey, new ArrayList<>());
					}
					if (synca) {
						List<List<String>> vs = dest.get(vkey);
						synchronized (vs) {
							vs.add(new ArrayList<>(data.values()));
						}
					} else {
						dest.get(vkey).add(new ArrayList<>(data.values()));
					}
				}
				if (n!=0 && ((i%n)==0)) {
					Thread.sleep(10);
				}
				i++;
			}
			return dest;
		} catch(IOException | InterruptedException e) {
			fail(e.toString());
			return null;
		}
	}
	
	protected Collection<List<String>> readUnformattedCsv(Collection<List<String>> dest, String file, boolean cabecera, boolean sync, int n) {
		return readUnformattedCsv(dest, file, cabecera, sync, n, 0, Integer.MAX_VALUE);
	}

	protected Collection<List<String>> readUnformattedCsv(Collection<List<String>> dest, String file, boolean cabecera, boolean sync, int n, int line, int nlines) {
		try(BufferedReader br = new BufferedReader(new FileReader(new File(file), StandardCharsets.UTF_8))) {
			CsvReader csvRdr = new CsvReader(br);
			if (cabecera)
				csvRdr.readData(); // descartar cabecera
			while(line>0 && csvRdr.readData()!=null) // descartar líneas iniciales
				line--;
			List<String> data;
			int i = 0;
			while(i<nlines && (data=csvRdr.readData())!=null ) {
				if (sync) {
					synchronized (dest) {
						dest.add(data);
					}
				} else {
					dest.add(data);
				}
				if (n!=0 && ((i%n)==0)) {
					Thread.sleep(10);
				}
				i++;
			}
			return dest;
		} catch(IOException | InterruptedException e) {
			fail(e.toString());
			return null;
		}
	}
	

	protected Collection<String[]> readUnformattedCsvAsArray(Collection<String[]> dest, String file, boolean cabecera, boolean sync, int n) {
		return readUnformattedCsvAsArray(dest, file, cabecera, sync, n, 0, Integer.MAX_VALUE);
	}
	
	protected Collection<String[]> readUnformattedCsvAsArray(Collection<String[]> dest, String file, boolean cabecera, boolean sync, int n, int line, int nlines) {
		try(BufferedReader br = new BufferedReader(new FileReader(new File(file), StandardCharsets.UTF_8))) {
			CsvReader csvRdr = new CsvReader(br);
			if (cabecera)
				csvRdr.readData(); // descartar cabecera
			while(line>0 && csvRdr.readData()!=null) // descartar líneas iniciales
				line--;
			List<String> data;
			int i = 0;
			while(i<nlines && (data=csvRdr.readData())!=null ) {
				if (sync) {
					synchronized (dest) {
						dest.add(data.toArray(new String[data.size()]));
					}
				} else {
					dest.add(data.toArray(new String[data.size()]));
				}
				if (n!=0 && ((i%n)==0)) {
					Thread.sleep(10);
				}
				i++;
			}
			return dest;
		} catch(IOException | InterruptedException e) {
			fail(e.toString());
			return null;
		}
	}
	
	protected String[] readUnformattedCsvAsBigArray(String[] dest, String file, boolean cabecera, boolean sync, int n) {
		return readUnformattedCsvAsBigArray(dest, file, cabecera, sync, n, 0, Integer.MAX_VALUE);
	}
	
	protected String[] readUnformattedCsvAsBigArray(String[] dest, String file, boolean cabecera, boolean sync, int n, int line, int nlines) {
		try(BufferedReader br = new BufferedReader(new FileReader(new File(file), StandardCharsets.UTF_8))) {
			CsvReader csvRdr = new CsvReader(br);
			if (cabecera)
				csvRdr.readData(); // descartar cabecera
			int fromLine = line;
			while(line>0 && csvRdr.readData()!=null) // descartar líneas iniciales
				line--;
			List<String> data;
			String [] array = null;
			int i = 0;
			while(i<nlines && (data=csvRdr.readData())!=null ) {
				if (array==null) {
					array = new String[data.size()];
				}
				data.toArray(array);
				if (sync) {
					synchronized (dest) {
						System.arraycopy(array, 0, dest, (i+fromLine)*array.length, array.length);
					}
				} else {
					System.arraycopy(array, 0, dest, (i+fromLine)*array.length, array.length);
				}
				if (n!=0 && ((i%n)==0)) {
					Thread.sleep(10);
				}
				i++;
			}
			return dest;
		} catch(IOException | InterruptedException e) {
			fail(e.toString());
			return null;
		}
	}
	
	protected BigList<String> readUnformattedCsvIntoBigArray(BigList<String> dest, String file, boolean cabecera, boolean sync, int n) {
		return readUnformattedCsvIntoBigArray(dest, file, cabecera, sync, n, 0, Integer.MAX_VALUE);
	}
	
	protected BigList<String> readUnformattedCsvIntoBigArray(BigList<String> dest, String file, boolean cabecera, boolean sync, int n, int line, int nlines) {
		try(BufferedReader br = new BufferedReader(new FileReader(new File(file), StandardCharsets.UTF_8))) {
			CsvReader csvRdr = new CsvReader(br);
			if (cabecera)
				csvRdr.readData(); // descartar cabecera
			while(line>0 && csvRdr.readData()!=null) // descartar líneas iniciales
				line--;
			List<String> data;
			String [] array = null;
			int i = 0;
			while(i<nlines && (data=csvRdr.readData())!=null ) {
				if (array==null) {
					array = new String[data.size()];
				}
				data.toArray(array);
				if (sync) {
					synchronized (dest) {
						dest.add(array);
					}
				} else {
					dest.add(array);
				}
				if (n!=0 && ((i%n)==0)) {
					Thread.sleep(10);
				}
				i++;
			}
			return dest;
		} catch(IOException | InterruptedException e) {
			fail(e.toString());
			return null;
		}
	}
	
	//@Test
	public void sequentialReadingListTest() {
		long t0 = new Date().getTime();
		List<List<String>> dest = new ArrayList<>();
		readUnformattedCsv(dest, "data-1.csv", true, false, 70);
		readUnformattedCsv(dest, "data-2.csv", true, false, 70);
		readUnformattedCsv(dest, "data-3.csv", true, false, 70);
		readUnformattedCsv(dest, "data-4.csv", true, false, 70);
		System.out.println(format("seq t: %.2f", (new Date().getTime()-t0)/1000.0));
		assertEquals(41715, dest.size());
	}
	
	@Test
	public void sequentialReadingListTestAlt() {
		long t0 = new Date().getTime();
		List<List<String>> dest = new ArrayList<>();
		readUnformattedCsv(dest, "shuffled.csv", true, false, 70, 0, 10000);
		readUnformattedCsv(dest, "shuffled.csv", true, false, 70, 10000, 10000);
		readUnformattedCsv(dest, "shuffled.csv", true, false, 70, 20000, 10000);
		readUnformattedCsv(dest, "shuffled.csv", true, false, 70, 30000, 11715);
		System.out.println(format("seq t: %.2f", (new Date().getTime()-t0)/1000.0));
		assertEquals(41715, dest.size());
	}
	
	@Test
	public void failingSynchronizationListTest() {
		long t0 = new Date().getTime();
		List<List<String>> dest = new ArrayList<>();
		List<Thread> ts = Arrays.stream(new String[] {"data-1", "data-2", "data-3", "data-4"})
				.map(s -> new Thread(() -> readUnformattedCsv(dest, s+".csv", true, false, 70)))
				.collect(Collectors.toList());
		ts.forEach(t -> t.start());
		ts.forEach(t -> { try { t.join(); } catch (InterruptedException e) { fail(e.toString()); } });
		System.out.println(format("multi-fails t: %.2f", (new Date().getTime()-t0)/1000.0));
		assertNotEquals(41715, dest.size());
	}

	@Test
	public void workingExternallySynchronizedListTest() {
		long t0 = new Date().getTime();
		List<List<String>> dest = new ArrayList<>();
		List<Thread> ts = Arrays.stream(new String[] {"data-1", "data-2", "data-3", "data-4"})
				.map(s -> new Thread(() -> readUnformattedCsv(dest, s+".csv", true, true, 70)))
				.collect(Collectors.toList());
		ts.forEach(t -> t.start());
		ts.forEach(t -> { try { t.join(); } catch (InterruptedException e) { fail(e.toString()); } });
		System.out.println(format("multi-works-ext t: %.2f", (new Date().getTime()-t0)/1000.0));
		assertEquals(41715, dest.size());
	}

	@Test
	public void workingSynchronizedListTest() {
		long t0 = new Date().getTime();
		List<List<String>> dest = Collections.synchronizedList(new ArrayList<>());
		List<Thread> ts = Arrays.stream(new String[] {"data-1", "data-2", "data-3", "data-4"})
				.map(s -> new Thread(() -> readUnformattedCsv(dest, s+".csv", true, false, 70)))
				.collect(Collectors.toList());
		ts.forEach(t -> t.start());
		ts.forEach(t -> { try { t.join(); } catch (InterruptedException e) { fail(e.toString()); } });
		System.out.println(format("multi-works t: %.2f", (new Date().getTime()-t0)/1000.0));
		assertEquals(41715, dest.size());
	}

	@Test
	public void failingSynchronizationMapTest() {
		long t0 = new Date().getTime();
		Map<String,List<List<String>>> dest = new HashMap<>();
		List<Thread> ts = Arrays.stream(new String[] {"data-s-1", "data-s-2", "data-s-3", "data-s-4"})
				.map(s -> new Thread(() -> readFormattedCsv(dest, "Year", s+".csv", null, false, false, 70)))
				.collect(Collectors.toList());
		ts.forEach(t -> t.start());
		ts.forEach(t -> { try { t.join(); } catch (InterruptedException e) { fail(e.toString()); } });
		System.out.println(format("multi-map-fails t: %.2f", (new Date().getTime()-t0)/1000.0));
		//System.out.println("Years: "+dest.keySet());
		//dest.keySet().forEach(k -> System.out.println(k+": "+dest.get(k).size()));
		int total = dest.values().stream().collect(Collectors.summingInt(List::size));
		assertNotEquals(41715, total);
		//System.out.println("Total: "+total);
	}

	@Test
	public void workingSynchronizationMapTest() {
		long t0 = new Date().getTime();
		Map<String,List<List<String>>> dest = new HashMap<>();
		List<Thread> ts = Arrays.stream(new String[] {"data-s-1", "data-s-2", "data-s-3", "data-s-4"})
				.map(s -> new Thread(() -> readFormattedCsv(dest, "Year", s+".csv", null, false, true, 70)))
				.collect(Collectors.toList());
		ts.forEach(t -> t.start());
		ts.forEach(t -> { try { t.join(); } catch (InterruptedException e) { fail(e.toString()); } });
		System.out.println(format("multi-map-works t: %.2f", (new Date().getTime()-t0)/1000.0));
		//System.out.println("Years: "+dest.keySet());
		//dest.keySet().forEach(k -> System.out.println(k+": "+dest.get(k).size()));
		int total = dest.values().stream().collect(Collectors.summingInt(List::size));
		assertEquals(41715, total);
		//System.out.println("Total: "+total);
	}

	@Test
	public void workingSynchronizationMapTest2() {
		long t0 = new Date().getTime();
		Map<String,List<List<String>>> dest = new HashMap<>();
		List<Thread> ts = Arrays.stream(new String[] {"data-s-1", "data-s-2", "data-s-3", "data-s-4"})
				.map(s -> new Thread(() -> readFormattedCsv(dest, "Industry_code_NZSIOC", s+".csv", null, true, false, 10)))
				.collect(Collectors.toList());
		ts.forEach(t -> t.start());
		ts.forEach(t -> { try { t.join(); } catch (InterruptedException e) { fail(e.toString()); } });
		System.out.println(format("multi-map-works-2 t: %.2f", (new Date().getTime()-t0)/1000.0));
		System.out.println("Codes: "+dest.keySet().size());
		//dest.keySet().forEach(k -> System.out.println(k+": "+dest.get(k).size()));
		int total = dest.values().stream().collect(Collectors.summingInt(List::size));
		assertEquals(41715, total);
		//System.out.println("Total: "+total);
	}
	
	// custom_1988-2020.csv. lines, 113.607.322
	// ym(Year + month), exp_imp(export: 1, import: 2), hs9(HS code), Customs, Country, Q1,Q2(quantity), Value(in thousands of yen)

	@Test
	public void workingSynchronizationMapTest2Alt() {
		long t0 = new Date().getTime();
		Map<String,List<List<String>>> dest = new HashMap<>();
		int nlines = lines("shuffled.csv")-1;
		List<Thread> ts = new ArrayList<>();
		int total = 0, nthreads = 10;
		for(int i = 0; i<nthreads; i++) {
			int from = total;
			int tnlines;
			if (i<(nthreads-1))
				tnlines = nlines/nthreads;
			else
				tnlines = nlines-total;
			ts.add(new Thread(() -> readFormattedCsv(dest, "Industry_code_NZSIOC", "shuffled.csv", null, true, false, 100, from, tnlines)));
			total += tnlines;
		}
		ts.forEach(t -> t.start());
		ts.forEach(t -> { try { t.join(); } catch (InterruptedException e) { fail(e.toString()); } });
		System.out.println(format("multi-map-works-2 t: %.2f", (new Date().getTime()-t0)/1000.0));
		System.out.println("Codes: "+dest.keySet().size());
		//dest.keySet().forEach(k -> System.out.println(k+": "+dest.get(k).size()));
		total = dest.values().stream().collect(Collectors.summingInt(List::size));
		assertEquals(41715, total);
		//System.out.println("Total: "+total);
	}

	@Test
	public void failingSynchronizationListBigTest() {
		long t0 = new Date().getTime();
		List<List<String>> dest = new ArrayList<>();
		int nlines = lines("bigone.csv"); //113607322; //lines("custom_1988_2020.csv");
		List<Thread> ts = new ArrayList<>();
		int total = 0, nthreads = 25;
		for(int i = 0; i<nthreads; i++) {
			int from = total;
			int tnlines;
			if (i<(nthreads-1))
				tnlines = nlines/nthreads;
			else
				tnlines = nlines-total;
			ts.add(new Thread(() -> readUnformattedCsv(dest, "bigone.csv", false, false, 0, from, tnlines)));
			total += tnlines;
		}
		ts.forEach(t -> t.start());
		ts.forEach(t -> { try { t.join(); } catch (InterruptedException e) { fail(e.toString()); } });
		System.out.println(format("multi-map-big-one t: %.2f", (new Date().getTime()-t0)/1000.0));
		//System.out.println("Codes: "+dest.keySet().size());
		//dest.keySet().forEach(k -> System.out.println(k+": "+dest.get(k).size()));
		//total = dest.values().stream().collect(Collectors.summingInt(List::size));
		assertNotEquals(nlines, dest.size());
		//System.out.println("Total: "+total);
	}

	@Test
	public void workingSynchronizationListBigTest() {
		long t0 = new Date().getTime();
		List<List<String>> dest = Collections.synchronizedList(new ArrayList<>());
		int nlines = lines("bigone.csv"); //113607322; //lines("custom_1988_2020.csv");
		List<Thread> ts = new ArrayList<>();
		int total = 0, nthreads = 25;
		for(int i = 0; i<nthreads; i++) {
			int from = total;
			int tnlines;
			if (i<(nthreads-1))
				tnlines = nlines/nthreads;
			else
				tnlines = nlines-total;
			ts.add(new Thread(() -> readUnformattedCsv(dest, "bigone.csv", false, false, 0, from, tnlines)));
			total += tnlines;
		}
		ts.forEach(t -> t.start());
		ts.forEach(t -> { try { t.join(); } catch (InterruptedException e) { fail(e.toString()); } });
		System.out.println(format("multi-map-big-one t: %.2f", (new Date().getTime()-t0)/1000.0));
		//System.out.println("Codes: "+dest.keySet().size());
		//dest.keySet().forEach(k -> System.out.println(k+": "+dest.get(k).size()));
		//total = dest.values().stream().collect(Collectors.summingInt(List::size));
		assertEquals(nlines, dest.size());
		//System.out.println("Total: "+total);
	}

	@Test
	public void workingExternalSynchronizationListBigTest() {
		long t0 = new Date().getTime();
		List<List<String>> dest = new ArrayList<>();
		int nlines = lines("bigone.csv"); //113607322; //lines("custom_1988_2020.csv");
		List<Thread> ts = new ArrayList<>();
		int total = 0, nthreads = 25;
		for(int i = 0; i<nthreads; i++) {
			int from = total;
			int tnlines;
			if (i<(nthreads-1))
				tnlines = nlines/nthreads;
			else
				tnlines = nlines-total;
			ts.add(new Thread(() -> readUnformattedCsv(dest, "bigone.csv", false, true, 0, from, tnlines)));
			total += tnlines;
		}
		ts.forEach(t -> t.start());
		ts.forEach(t -> { try { t.join(); } catch (InterruptedException e) { fail(e.toString()); } });
		System.out.println(format("multi-map-big-one t: %.2f", (new Date().getTime()-t0)/1000.0));
		//System.out.println("Codes: "+dest.keySet().size());
		//dest.keySet().forEach(k -> System.out.println(k+": "+dest.get(k).size()));
		//total = dest.values().stream().collect(Collectors.summingInt(List::size));
		assertEquals(nlines, dest.size());
		//System.out.println("Total: "+total);
	}
	
	protected Comparator<List<String>> BIGONE_COMPARATOR = (a,b)-> {
		int c = a.get(0)!=null?a.get(0).compareTo(b.get(0)):0;
		if (c==0) {
			c = a.get(1)!=null?a.get(1).compareTo(b.get(1)):0;
			if (c==0) {
				c = a.get(7)!=null && b.get(7)!=null?Integer.compare(Integer.parseInt(a.get(7), 10), Integer.parseInt(b.get(7), 10)):0;
				if (c==0) {
					c = a.get(6)!=null && b.get(6)!=null?Integer.compare(Integer.parseInt(a.get(6), 10), Integer.parseInt(b.get(6), 10)):0;
					if (c==0) {
						c = a.get(2)!=null?a.get(2).compareTo(b.get(2)):0;
						if (c==0) {
							c = a.get(3)!=null?a.get(3).compareTo(b.get(3)):0;
							if (c==0) {
								c = a.get(4)!=null?a.get(4).compareTo(b.get(4)):0;
							}
						}
					}
				}
			}
		}
		return c;
	};

	protected Comparator<String[]> BIGONE_COMPARATOR_ARRAY = (a,b)-> {
		int c = a[0]!=null?a[0].compareTo(b[0]):0;
		if (c==0) {
			c = a[1]!=null?a[1].compareTo(b[1]):0;
			if (c==0) {
				c = a[7]!=null && b[7]!=null?Integer.compare(Integer.parseInt(a[7], 10), Integer.parseInt(b[7], 10)):0;
				if (c==0) {
					c = a[6]!=null && b[6]!=null?Integer.compare(Integer.parseInt(a[6], 10), Integer.parseInt(b[6], 10)):0;
					if (c==0) {
						c = a[2]!=null?a[2].compareTo(b[2]):0;
						if (c==0) {
							c = a[3]!=null?a[3].compareTo(b[3]):0;
							if (c==0) {
								c = a[4]!=null?a[4].compareTo(b[4]):0;
							}
						}
					}
				}
			}
		}
		return c;
	};

	@Test
	public void workingSynchronizationSortedSetBigTest() {
		long t0 = new Date().getTime();
		java.util.SortedSet<List<String>> dest = Collections.synchronizedSortedSet(new TreeSet<>(BIGONE_COMPARATOR));
		int nlines = lines("bigone.csv"); //113607322; //lines("custom_1988_2020.csv");
		List<Thread> ts = new ArrayList<>();
		int total = 0, nthreads = 25;
		for(int i = 0; i<nthreads; i++) {
			int from = total;
			int tnlines;
			if (i<(nthreads-1))
				tnlines = nlines/nthreads;
			else
				tnlines = nlines-total;
			ts.add(new Thread(() -> readUnformattedCsv(dest, "bigone.csv", false, false, 0, from, tnlines)));
			total += tnlines;
		}
		ts.forEach(t -> t.start());
		ts.forEach(t -> { try { t.join(); } catch (InterruptedException e) { fail(e.toString()); } });
		System.out.println(format("multi-map-big-one t: %.2f", (new Date().getTime()-t0)/1000.0));
		//System.out.println("Codes: "+dest.keySet().size());
		//dest.keySet().forEach(k -> System.out.println(k+": "+dest.get(k).size()));
		//total = dest.values().stream().collect(Collectors.summingInt(List::size));
		assertEquals(nlines, dest.size());
		System.out.println("Biggest: "+dest.first());
		System.out.println("Smallest: "+dest.last());
	}

	@Test
	public void workingExternalSynchronizationSortedSetBigTest() {
		long t0 = new Date().getTime();
		java.util.SortedSet<List<String>> dest = new TreeSet<>(BIGONE_COMPARATOR);
		int nlines = lines("bigone.csv"); //113607322; //lines("custom_1988_2020.csv");
		List<Thread> ts = new ArrayList<>();
		int total = 0, nthreads = 25;
		for(int i = 0; i<nthreads; i++) {
			int from = total;
			int tnlines;
			if (i<(nthreads-1))
				tnlines = nlines/nthreads;
			else
				tnlines = nlines-total;
			ts.add(new Thread(() -> readUnformattedCsv(dest, "bigone.csv", false, true, 0, from, tnlines)));
			total += tnlines;
		}
		ts.forEach(t -> t.start());
		ts.forEach(t -> { try { t.join(); } catch (InterruptedException e) { fail(e.toString()); } });
		System.out.println(format("multi-map-big-one t: %.2f", (new Date().getTime()-t0)/1000.0));
		//System.out.println("Codes: "+dest.keySet().size());
		//dest.keySet().forEach(k -> System.out.println(k+": "+dest.get(k).size()));
		//total = dest.values().stream().collect(Collectors.summingInt(List::size));
		assertEquals(nlines, dest.size());
		System.out.println("Biggest: "+dest.first());
		System.out.println("Smallest: "+dest.last());
	}
	
	String [] seq(int from, int n) {
		String [] r = new String[n];
		for(int i = 0; i<n; i++) {
			if ((from+n)<10)
				r[i] = format("%d", from+i);
			else if ((from+n)<100)
				r[i] = format("%02d", from+i);
			else if ((from+n)<1000)
				r[i] = format("%03d", from+i);
			else if ((from+n)<10000)
				r[i] = format("%04d", from+i);
		}
		return r;
	}

	@Test
	public void workingExternalSynchronizationSortedSetBigAltTest() {
		long t0 = new Date().getTime();
		java.util.SortedSet<String[]> dest = new TreeSet<>(BIGONE_COMPARATOR_ARRAY);
		List<Thread> ts = Arrays.stream(seq(0, 12)).map(s -> new Thread(() -> readUnformattedCsvAsArray(dest, "bigone"+s+".csv", false, true, 0, 0, 750000))).collect(Collectors.toList());
		ts.forEach(t -> t.start());
		ts.forEach(t -> { try { t.join(); } catch (InterruptedException e) { fail(e.toString()); } });
		System.out.println(format("multi-map-big-alt-one t: %.2f", (new Date().getTime()-t0)/1000.0));
		System.out.println(format("%.2f Mb", Runtime.getRuntime().freeMemory()/(1024.0*1024.0)));
		//System.out.println("Codes: "+dest.keySet().size());
		//dest.keySet().forEach(k -> System.out.println(k+": "+dest.get(k).size()));
		//total = dest.values().stream().collect(Collectors.summingInt(List::size));
		//assertEquals(11360732, dest.size());
		System.out.println("Biggest: "+Arrays.asList(dest.first()));
		System.out.println("Smallest: "+Arrays.asList(dest.last()));
	}

	@Test
	public void biggestArrayTest() {
		String [] array = new String[11360732*8];
		for(int i = 0; i<array.length; i++)
			array[i] = new String("abcdefg");
		System.out.println(array[(int)Math.round(Math.floor(array.length*Math.random()))]);
		System.out.println(format("%.2f Mb", Runtime.getRuntime().freeMemory()/(1024.0*1024.0)));
	}

	@Test
	public void bigListOfArraysTest() {
		List<String []> list = new ArrayList<>();
		for(int i = 0; i<11360732; i++)
			list.add(new String[] { new String("abcdefg"), new String("abcdefg"), new String("abcdefg"), new String("abcdefg"), new String("abcdefg"), new String("abcdefg"), new String("abcdefg"), new String("abcdefg")});
		System.out.println(list.get((int)Math.round(Math.floor(list.size()*Math.random())))[4]);
		System.out.println(format("%.2f Mb", Runtime.getRuntime().freeMemory()/(1024.0*1024.0)));
	}
	
	@Test
	public void readCsvIntoBigArrayTest() {
		BigList<String> dest = new BigList<String>(8, 11500000 /*11360732*/, String.class);
		long t0 = new Date().getTime();
		List<Thread> ts = Arrays.stream(seq(0, 12)).map(s -> new Thread(() -> readUnformattedCsvIntoBigArray(dest, "bigone"+s+".csv", false, true, 0, 0, 850000))).collect(Collectors.toList());
		ts.forEach(t -> t.start());
		ts.forEach(t -> { try { t.join(); } catch (InterruptedException e) { fail(e.toString()); } });
		System.out.println(format("working-csv-into-big-array t: %.2f", (new Date().getTime()-t0)/1000.0));
		System.out.println(format("%.2f Mb", Runtime.getRuntime().freeMemory()/(1024.0*1024.0)));
		System.out.println(format("%d/%d", dest.size(), dest.bufferSize()));
	}

}
