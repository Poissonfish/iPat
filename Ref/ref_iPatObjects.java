
	    	}catch (IOException e1) {e1.printStackTrace();}	
			if(iPatPanel.debug){
				// Print command	
				textarea.append("Command: \n");
				for (int i = 0; i < command.length; i++) textarea.append(command[i] + " ");
				textarea.append("\nFor R: \n");
				textarea.append("project=\""+command[2]+"\"\n");
				textarea.append("wd=\""+command[3]+"\"\n");
				textarea.append("lib=\""+command[4]+"\"\n");
				textarea.append("format=\""+command[5]+"\"\n");
				textarea.append("ms=as.numeric(\""+command[6]+"\")\n");
				textarea.append("maf=as.numeric(\""+command[7]+"\")\n");
				textarea.append("Y.path=\""+command[8]+"\"\n");
				textarea.append("Y.index=\""+command[9]+"\"\n");
				textarea.append("GD.path=\""+command[10]+"\"\n");
				textarea.append("GM.path=\""+command[11]+"\"\n");
				textarea.append("C.path=\""+command[12]+"\"\n");
				textarea.append("C.index=\""+command[13]+"\"\n");
				textarea.append("K.path=\""+command[14]+"\"\n");
				textarea.append("FAM.path=\""+command[15]+"\"\n");
				textarea.append("BIM.path=\""+command[16]+"\"\n");}
			textarea.append("\n");
			textarea.setCaretPosition(textarea.getDocument().getLength());
	       	System.out.println("done");
