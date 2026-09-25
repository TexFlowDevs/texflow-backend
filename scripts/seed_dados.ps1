$baseUrl = "http://localhost:8080/api"

$empresas = @(
    @{ cnpj = "12.345.678/0001-90"; nomeFantasia = "Moda Bella Confeccoes"; contato = @{ nome = "Ana Souza"; telefone = "47999990001" } },
    @{ cnpj = "23.456.789/0001-01"; nomeFantasia = "Vestuario Nobre SA"; contato = @{ nome = "Bruno Lima"; telefone = "47999990002" } },
    @{ cnpj = "34.567.890/0001-12"; nomeFantasia = "Baby Chic Infantil"; contato = @{ nome = "Carla Nunes"; telefone = "47999990003" } }
)

$empresaIds = @()
foreach ($empresa in $empresas) {
    $body = $empresa | ConvertTo-Json -Depth 5
    $resposta = Invoke-RestMethod -Uri "$baseUrl/empresas" -Method Post -ContentType "application/json" -Body $body
    $empresaIds += $resposta.id
    Write-Host "Empresa criada: $($resposta.nomeFantasia) (id $($resposta.id))"
}

$referencias = @(
    @{ nome = "MOD-FEM-045" },
    @{ nome = "MOD-MASC-018" },
    @{ nome = "MOD-INF-032" }
)

$referenciaIds = @()
foreach ($referencia in $referencias) {
    $body = $referencia | ConvertTo-Json
    $resposta = Invoke-RestMethod -Uri "$baseUrl/referencias" -Method Post -ContentType "application/json" -Body $body
    $referenciaIds += $resposta.id
    Write-Host "Referencia criada: $($resposta.nome) (id $($resposta.id))"
}

$operacoes = @(
    @{
        referencia = @{ id = $referenciaIds[0] }
        cliente = @{ id = $empresaIds[0] }
        dataEntrega = "2026-10-15"
        status = "emAndamento"
        gradePedido = @(@{ descricao = "P"; quantidade = 60 }, @{ descricao = "M"; quantidade = 90 })
        gradeFabricada = @(@{ descricao = "P"; quantidade = 40 }, @{ descricao = "M"; quantidade = 53 })
    },
    @{
        referencia = @{ id = $referenciaIds[1] }
        cliente = @{ id = $empresaIds[1] }
        dataEntrega = "2026-10-21"
        status = "emAndamento"
        gradePedido = @(@{ descricao = "M"; quantidade = 50 }, @{ descricao = "G"; quantidade = 30 })
        gradeFabricada = @(@{ descricao = "M"; quantidade = 12 }, @{ descricao = "G"; quantidade = 8 })
    },
    @{
        referencia = @{ id = $referenciaIds[2] }
        cliente = @{ id = $empresaIds[2] }
        dataEntrega = "2026-09-30"
        status = "concluido"
        gradePedido = @(@{ descricao = "Unico"; quantidade = 100 })
        gradeFabricada = @(@{ descricao = "Unico"; quantidade = 100 })
    },
    @{
        referencia = @{ id = $referenciaIds[0] }
        cliente = @{ id = $empresaIds[1] }
        dataEntrega = "2026-10-05"
        status = "cancelado"
        gradePedido = @(@{ descricao = "P"; quantidade = 60 })
        gradeFabricada = @()
    },
    @{
        referencia = @{ id = $referenciaIds[1] }
        cliente = @{ id = $empresaIds[0] }
        dataEntrega = "2026-11-10"
        status = "naoIniciado"
        gradePedido = @(@{ descricao = "P"; quantidade = 40 }, @{ descricao = "M"; quantidade = 40 })
        gradeFabricada = @()
    },
    @{
        referencia = @{ id = $referenciaIds[2] }
        cliente = @{ id = $empresaIds[2] }
        dataEntrega = "2026-11-20"
        status = "naoIniciado"
        gradePedido = @(@{ descricao = "Unico"; quantidade = 70 })
        gradeFabricada = @()
    }
)

foreach ($operacao in $operacoes) {
    $body = $operacao | ConvertTo-Json -Depth 5
    $resposta = Invoke-RestMethod -Uri "$baseUrl/operacoes" -Method Post -ContentType "application/json" -Body $body
    Write-Host "Operacao criada: id $($resposta.id), status $($resposta.status)"
}

Write-Host "Concluido."
