package org.api.domain.model;

public enum SubStatus {

    LOCKED("LOCKED, password expired"), // BLOQUEADO, SENHA EXPIRADA APOS 3 MESES, RECUPERE UMA NOVA SENHA
    ON_ALERT("ON ALERT, the password is about to expire"), // QUANDO A SENHA TIVER PRESTES A EXPIRAR TANTO NO CASO DE ATUALIZAR SENHA
    // MANUAL COMO APOS 3 MESES DE USO DA MESMA SENHA
    IN_NON_COMPLIANCE("IN NON-COMPLIANCE, you haven't changed your password yet, the deadline is about to expire."), // NAO COMPLIANCE, PRECISA
    // ALTERAR A SENHA MANUALMENTE ANTES DOS 7 DIAS
    BLOCKED("BLOCKED, user did not update password after first access"), // BLOQUEADO, USUARIO NAO ATUALIZOU A SENHA MANUALMENTE APOS OS 7 DIAS
    UNLOCKED("UNLOCKED, user compliant"); // DESBLOQUEADO E EM COMPLIANCE, SENHA E USUARIO OK


    private String substatus_description;

    private SubStatus(String substatus_description) {this.substatus_description = substatus_description;}

    public String getSubStatus_description() {
        return substatus_description;
    }

}
