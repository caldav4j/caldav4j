/*
 * Copyright (c) 2001 World Wide Web Consortium,
 * (Massachusetts Institute of Technology, Institut National de
 * Recherche en Informatique et en Automatique, Keio University). All
 * Rights Reserved. This program is distributed under the W3C's Software
 * Intellectual Property License. This program is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE.
 * See W3C License http://www.w3.org/Consortium/Legal/ for more details.
 */

package org.apache.xerces.dom3.as;

/**
 * @deprecated
 * Models a general entity declaration in an abstract schema. The abstract 
 * schema does not handle any parameter entity. It is assumed that the 
 * parameter entities are expanded by the implementation as the abstract 
 * schema is built.
 * <p>See also the <a href='http://www.w3.org/TR/2001/WD-DOM-Level-3-ASLS-20011025'>Document Object Model (DOM) Level 3 Abstract Schemas and Load
and Save Specification</a>.
 */
public interface ASEntityDeclaration extends ASObject {
    // EntityType
    /**
     * constant defining an internal entity.
     */
    public static final short INTERNAL_ENTITY           = 1;
    /**
     * constant defining an external entity.
     */
    public static final short EXTERNAL_ENTITY           = 2;

    /**
     * The type of the entity as defined above.
     */
    public short getEntityType();
    /**
     * The type of the entity as defined above.
     */
    public void setEntityType(short entityType);

    /**
     * The replacement text for the internal entity. The entity references 
     * within the replacement text are kept intact. For an entity of type 
     * <code>EXTERNAL_ENTITY</code>, this is <code>null</code>.
     */
    public String getEntityValue();
    /**
     * The replacement text for the internal entity. The entity references 
     * within the replacement text are kept intact. For an entity of type 
     * <code>EXTERNAL_ENTITY</code>, this is <code>null</code>.
     */
    public void setEntityValue(String entityValue);

    /**
     * the URI reference representing the system identifier for the notation 
     * declaration, if present, <code>null</code> otherwise.
     */
    public String getSystemId();
    /**
     * the URI reference representing the system identifier for the notation 
     * declaration, if present, <code>null</code> otherwise.
     */
    public void setSystemId(String systemId);

    /**
     * The string representing the public identifier for this notation 
     * declaration, if present; <code>null</code> otherwise.
     */
    public String getPublicId();
    /**
     * The string representing the public identifier for this notation 
     * declaration, if present; <code>null</code> otherwise.
     */
    public void setPublicId(String publicId);

}
