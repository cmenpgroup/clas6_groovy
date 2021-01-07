import org.jlab.jnp.hipo4.data.*;
import org.jlab.jnp.hipo4.io.*;


//HipoWriter output = new HipoWriter();
//output.open("outfile.hipo");
HipoReader  reader = new HipoReader();
reader.open(args[0]);
HipoWriter output = new HipoWriter(reader.getSchemaFactory());
String outfile = "output/"+args[0] + "_skimmed.hipo";
output.open(outfile);
Bank bank = new Bank(reader.getSchemaFactory().getSchema("EVENT::particle"));
Event event = new Event();
while(reader.hasNext()) {
        reader.nextEvent(event);
        boolean electron =false;
        boolean positron =false;
        boolean proton =false;
        event.read(bank);
        for(int i=0;i<bank.getRows();i++){
                 int pid = bank.getInt("pid",i);
                 //int status = bank.getInt("status",i);
                 //System.out.println(pid);
                 if(pid==11)electron=true;
                 if(pid==-11)positron=true;
                 if(pid==2212)proton=true;
        }
	if(electron){
                output.addEvent(event);
        }

                                    
}
reader.close();
output.close();
            
