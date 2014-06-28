package pl.touk.sputnik.connector.stash.json;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 {
         "text": "A pithy comment on commit.",
     }
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    private String text;
}
