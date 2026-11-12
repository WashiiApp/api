package br.com.washii.api.config;

import br.com.washii.api.model.CategoriaServico;
import br.com.washii.api.model.CategoriaVeiculo;
import br.com.washii.api.model.DiasSemana;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class LocalCacheConfig {
    // Cache para salvar dados estáticos localmente com o Caffeine

    @Bean("categoriaServicoCacheBean")
    public Cache<UUID, CategoriaServico> categoriaServicoCache() {
        return Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(24, TimeUnit.HOURS)
                .recordStats()
                .build();
    }

    @Bean("categoriaVeiculoCacheBean")
    public Cache<UUID, CategoriaVeiculo> categoriaVeiculoCache() {
        return Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(24, TimeUnit.HOURS)
                .recordStats()
                .build();
    }

    @Bean("diasSemanaCacheBean")
    public Cache<UUID, DiasSemana> diasSemanaCache() {
        return Caffeine.newBuilder()
                .maximumSize(10)
                .expireAfterWrite(24, TimeUnit.HOURS)
                .recordStats()
                .build();
    }
}
