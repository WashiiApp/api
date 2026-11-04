package br.com.washii.api.service;

public interface StorageProvider {
    String upload(String caminhoArquivo, byte[] conteudo, String contentType);
    void excluir(String caminhoArquivo);
}

