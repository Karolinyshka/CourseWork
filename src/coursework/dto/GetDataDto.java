package coursework.dto;

import java.io.Serializable;

public class GetDataDto implements Serializable {
    private String query;
    private String field2;

    public GetDataDto(String query, String field2) {
        this.query = query;
        this.field2 = field2;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getField2() {
        return field2;
    }

    public void setField2(String field2) {
        this.field2 = field2;
    }

    @Override
    public String toString() {
        return "GetDataDto{" +
                "query='" + query + '\'' +
                ", field2='" + field2 + '\'' +
                '}';
    }
}
