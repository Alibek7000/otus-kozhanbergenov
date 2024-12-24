package kz.entity;

import kz.annotation.RepositoryField;
import kz.annotation.RepositoryIdField;
import kz.annotation.RepositoryTable;
import lombok.Data;
import lombok.NoArgsConstructor;

@RepositoryTable(title = "accounts")
@NoArgsConstructor
@Data
public class Account {
    @RepositoryIdField
    @RepositoryField
    private Long id;

    @RepositoryField
    private Long amount;

    @RepositoryField(name = "tp")
    private String accountType;

    @RepositoryField
    private String status;

    public Account(Long amount, String accountType, String status) {
        this.amount = amount;
        this.accountType = accountType;
        this.status = status;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", amount=" + amount +
                ", accountType='" + accountType + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
