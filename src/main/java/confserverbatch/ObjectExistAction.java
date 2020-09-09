/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package confserverbatch;

/**
 *
 * @author stepan_sydoruk
 */
public enum ObjectExistAction {
    UNKNOWN("Unknown"),
    REUSE("Reuse"),
    RECREATE("Recreate"),
    FAIL("Fail"),
    SKIP("Skip");

    private final String name;

    private ObjectExistAction(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return (otherName == null) ? false : name.equals(otherName);
    }

    @Override
    public String toString() {
        return this.name;
    }

}
