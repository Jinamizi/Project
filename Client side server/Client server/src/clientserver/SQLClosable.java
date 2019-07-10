/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientserver;

import java.sql.SQLException;

public interface SQLClosable extends AutoCloseable {
    public void close() throws SQLException;
}
