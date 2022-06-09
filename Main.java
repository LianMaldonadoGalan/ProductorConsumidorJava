import java.util.concurrent.Semaphore;

public class Main {
	
	static int buffersize = 22;
	static int minItems = 2;
	static int maxItems = 5;
	
	static Semaphore contadorllenado = new Semaphore(0);
	static Semaphore contadorvaciado = new Semaphore(buffersize);
	static Semaphore bufferactivo = new Semaphore(1);
	
	static int semaforoCantItems = 0;
	
	static int lugarActualProductor = 0;
	static int lugarActualConsumidor = 0;
	
	static char buffer[] = new char[buffersize];


	public static void main(String[] args) {
		for(int i=0;i<=buffersize-1;i++) {
			
			buffer[i] = '_';
		}
		
		
		class productor implements Runnable{

			@Override
			public synchronized void run() {
				
				while (true){
					
					if(semaforoCantItems>=buffersize) {
			            try {
                            System.out.println ("El productor entra en espera");
                            this.wait (2000); // El hilo espera y libera el bloqueo
				       } catch (InterruptedException e) {
				           e.printStackTrace();
				       }
					}
					
					System.out.println ("La cantidad de items actuales es: " +(buffersize - contadorvaciado.availablePermits()));
					
					try {
						
						bufferactivo.acquire();
						System.out.println ("Productor intenta entrar al buffer");
							
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					System.out.println ("Productor entra al buffer");
					
					int cant = (int) (Math.random() * (maxItems - minItems) + minItems);
					
					if((cant+semaforoCantItems)>buffersize) {
						cant = buffersize-semaforoCantItems;
					}
					
			        try {
			        	
						contadorvaciado.acquire(cant);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
			        
			        System.out.println ("Productor obtuvo permisos");
			        
			        producirItem(cant);
			        
			        
			        
			        poneritemenbuffer(cant);
			            
			        contadorllenado.release(cant);
			        
			        semaforoCantItems = semaforoCantItems + cant;
			        
			        System.out.println ("Productor creo items");
			        
			        imprimebuffer();
			        
			        bufferactivo.release();
			        
			        try {
	                      
	                      Thread.sleep(1000); 
	                      
		            } catch (InterruptedException e) {
		                e.printStackTrace();
		            }
				
			}
				
			}

			private void poneritemenbuffer(int cant) {
				
				for(int i = 0; i<cant; i++) {
					
					buffer[lugarActualProductor] = '*';
					lugarActualProductor++;
					
					if(lugarActualProductor==buffersize) {
						lugarActualProductor = 0;
					}
					
				}
				
			}

			private void producirItem(int cant) {
				System.out.println("El productor ha creado "+cant+ " items.");
			}
		
		}
		
		
		
		class consumidor implements Runnable{
			
			@Override
			public synchronized void run() {
				
				while (true){
					
					if(semaforoCantItems==0) {
						 try {
	                            System.out.println ("El consumidor entra en espera");
	                            this.wait (4000); // El hilo espera y libera el bloqueo
					       } catch (InterruptedException e) {
					           e.printStackTrace();
					       }
					}
					
					System.out.println ("La cantidad de items actuales es: " +(buffersize - contadorvaciado.availablePermits()));
					
					
					try {
						
						bufferactivo.acquire();
						System.out.println ("Consumidor intenta entrar al buffer");
						
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					System.out.println ("Consumidor entra al buffer");
					
					int cant = (int) (Math.random() * (maxItems - minItems) + minItems);
					
					if((semaforoCantItems-cant)<0) {
						cant = semaforoCantItems;
					}
					
					 try {
							contadorllenado.acquire(cant);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					 
					 
					 
					 System.out.println ("Consumidor obtuvo permisos");
					 
					consumirItem(cant);
			        
			        quitaritemenbuffer(cant);
			            
			        contadorvaciado.release(cant);
			        
			        semaforoCantItems = semaforoCantItems - cant;
			        
			        System.out.println ("Consumidor quito items");
			        
			        imprimebuffer();
			        
			        bufferactivo.release();
			        
			        try {
	                      
	                      Thread.sleep(1000); 
	                      
		            } catch (InterruptedException e) {
		                e.printStackTrace();
		            }
					
			}
				
			}

			private void quitaritemenbuffer(int cant) {
				
				for(int i = 0; i<cant; i++) {
					
					buffer[lugarActualConsumidor] = '_';
					lugarActualConsumidor++;
					
					if(lugarActualConsumidor==buffersize) {
						lugarActualConsumidor = 0;
					}
					
				}
				
			}

			private void consumirItem(int cant) {
				System.out.println("El consumidor ha quitado "+cant+ " items.");
			}
				
		}
		
		
		productor p1 = new productor();
		
		consumidor c1 = new consumidor();
		
		Thread consumidor = new Thread(p1);
		
		Thread productor = new Thread(c1);
		
		consumidor.start();
		
		productor.start();
		
		
	}

	private static void imprimebuffer() {
		System.out.println("Imprimiendo buffer:");
		
		for(int i=0;i<=buffersize-1;i++) {
			if(i<=8) {
				System.out.print(buffer[i]+ " ");
			}else {
				System.out.print(buffer[i]+ "  ");
			}
		}
		
		System.out.println("");
		for(int o=1;o<=buffersize;o++) {
			System.out.print(o+ " ");
		}
		System.out.println("\n");
	}
}
