/*
 * Copyright (C) 2022 hcadavid
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package rug.icdtools.extensions.bitwiseencoding;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;
import rug.icdtools.interfacing.localcommands.CommandExecutionException;
import rug.icdtools.interfacing.localcommands.CommandGeneratedException;

/**
 *
 * @author hcadavid
 */
public class KaitaiRunTest {
    
    private static String getKaitaiSpecId(File f) throws FileNotFoundException{
        Yaml yaml = new Yaml();
        InputStream inputStream = new FileInputStream(f);

        Map<String, Object> obj = yaml.load(inputStream);
        System.out.println(obj);
        Object metadata=obj.get("meta");
        
        String specId=null;
        
        if (metadata==null || !(metadata instanceof Map)){
            throw new RuntimeException("no kaitai metadata");
        }
        else{            
            Object id = ((Map<String,Object>)metadata).get("id");
            if (id==null){
                throw new RuntimeException("no kaitai metadata");
            }
            else{
               specId = (String)id; 
            }
        }
        
        return specId;
    }
    
    public static void main(String a[]) throws CommandGeneratedException, CommandExecutionException, IOException{
        
        File f= new File("/home/hcadavid/RUG-local/proofs-of-concept/rover-example/test/test.ksy");
        
        String kaitaiSpecId=getKaitaiSpecId(f);
        
        System.out.println("ID:"+kaitaiSpecId);
        
        Path path = Files.createTempDirectory("kaitai-svg-output-");
        System.out.println(path.toString());                
                       
        //.runCommand("/home/hcadavid/apps/kaitai/kaitai-struct-compiler-0.10/bin/kaitai-struct-compiler", "--target", "graphviz", "--outdir", path.toString(), f.toString());
        //tempOutputFolder.runCommand("dot", "-Tsvg", path.resolve(kaitaiSpecId+".dot").toString(), "-O" );
        
        
        
    }
    
}
