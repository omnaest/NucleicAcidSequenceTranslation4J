# NucleicAcidSequenceTranslation4J
DNA/RNA translation utils

# Example

	assertEquals("MPPVGGKKAKK", TranslationUtils.translate(0, NucleicAcidCodeSequence.valueOf("ATGCCACCCGTTGGGGGCAAAAAGGCCAAGAAG"))
																					 .asAminoAcidCodeSequence()
																					 .toString());
																					 
	assertEquals(StringUtils.reverse("FAIS"), TranslationUtils.translateReverse(0, NucleicAcidCodeSequence.valueOf("GCGATATCGCAAA"))
																										  .asAminoAcidCodeSequence()
																										  .toString());																				 

# Maven Snapshots

    <dependency>
      <groupId>org.omnaest.genomics</groupId>
      <artifactId>NucleicAcidSequenceTranslation4J</artifactId>
      <version>0.0.1-SNAPSHOT</version>
    </dependency>
    
    <repositories>
    	<repository>
    		<id>ossrh</id>
    		<url>https://oss.sonatype.org/content/repositories/snapshots</url>
    		<snapshots>
    			<enabled>true</enabled>
    		</snapshots>
    	</repository>
    </repositories>
