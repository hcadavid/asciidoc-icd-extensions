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
package rug.icdtools.dashboard.postprocessors;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.asciidoctor.ast.Document;
import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.Postprocessor;
import rug.icdtools.logging.DocProcessLogger;
import rug.icdtools.logging.Severity;

/**
 *
 * @author hcadavid
 */
public class DocumentMetadataPostProcessor extends Postprocessor {

    private static boolean firstDocVisited = false;

    private static Set<String> notVisited;

    @Override
    public String process(Document dcmnt, String output) {

        StructuralNode stdoc = (StructuralNode) dcmnt;
        
        String currentFilePath = Paths.get(stdoc.getSourceLocation().getDir()).resolve(stdoc.getSourceLocation().getFile()).toString();
        
        if (!firstDocVisited) {
            
            try {
                try ( Stream<Path> walk = Files.walk(Paths.get(((StructuralNode) dcmnt).getSourceLocation().getDir()))) {
                    notVisited = walk
                            .filter(p -> !Files.isDirectory(p))
                            //.peek(p -> System.out.println(p+","+p.toString().endsWith(".adoc")))
                            .filter(f -> f.toString().endsWith(".adoc"))
                            .map(p -> p.toString())
                            .collect(Collectors.toSet());
                }

            } catch (IOException ex) {
                Logger.getLogger(DocumentMetadataPostProcessor.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                firstDocVisited = true;
                DocProcessLogger.getInstance().log(">>>>>>"+notVisited.size(),Severity.INFO);
            }
        }
        
        DocProcessLogger.getInstance().log("Removing from documents to be visited :"+currentFilePath+":"+notVisited.remove(currentFilePath),Severity.INFO);
               
        //the last element of the building process
        if (notVisited.isEmpty()) {
            DocProcessLogger.getInstance().log(">>>POSTING METADATA FROM "+this.hashCode(), Severity.INFO);
        }
        return output;

    }

}
