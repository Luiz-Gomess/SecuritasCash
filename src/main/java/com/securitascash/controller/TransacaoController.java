package com.securitascash.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import com.securitascash.dto.comentario.ComentarioForm;
import com.securitascash.dto.transacao.TransacaoForm;
import com.securitascash.dto.usuario.UsuarioSessao;
import com.securitascash.model.Comentario;
import com.securitascash.model.Transacao;
import com.securitascash.model.conta.Conta;
import com.securitascash.service.conta.ContaService;
import com.securitascash.service.transacao.TransacaoService;
import com.securitascash.service.usuario.UsuarioService;
import com.securitascash.utils.Utils;

import jakarta.servlet.http.HttpSession;


@Controller
@RequestMapping("contas/{id}/transacoes")
public class TransacaoController {

    @Autowired
    TransacaoService transacaoService;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    ContaService contaService;

    //TODO: Adicionar os outros métodos de comentário: UPDATE E DELETE

    @GetMapping
    public String listarTransacoesPorConta(@PathVariable Long id, HttpSession session, Model model) throws Exception {

        UsuarioSessao usuarioSessao = Utils.getUsuarioSessao(session);
        Conta conta = contaService.buscarPorId(id);

        // Verifica se a conta pertence ao usuário logado
        if (!conta.getUsuario().getId().equals(usuarioSessao.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado");
        }

        List<Transacao> transacoes = transacaoService.listarTransacoes(id);
        model.addAttribute("transacoes", transacoes);
        model.addAttribute("conta", conta);
        model.addAttribute("contaId", id);
        return "transacoes";
    }


    @GetMapping("/{transacaoId}/comentarios")
    public ModelAndView listarComentarios (@PathVariable Long transacaoId, ModelAndView mav, @PathVariable("id") Long contaId) {

        Transacao transacao = transacaoService.buscarTransacaoPorId(transacaoId);
        
        mav.setViewName("transacoes/comentarios");

        mav.addObject("comentarios", transacao.getComentarios());
        mav.addObject("contaId", contaId);
        mav.addObject("transacao", transacao);
        mav.addObject("comentarioForm", new ComentarioForm());
        
        return mav;
    }

    @PostMapping("/{transacaoId}/comentarios")
    public String salvarComentario(@ModelAttribute ComentarioForm comentarioForm,
                                @RequestParam Long transacaoId, @PathVariable("id") Long contaId) {

        Transacao transacao = transacaoService.buscarTransacaoPorId(transacaoId);

        Comentario comentario = new Comentario();
        comentario.setTexto(comentarioForm.getTexto());
        comentario.setTransacao(transacao);

        transacaoService.adicionarComentario(transacao.getId(), comentario);

        return "redirect:/contas/" + contaId + "/transacoes/" + transacaoId + "/comentarios" ;
    }

    @GetMapping("criar")
    public String criarTransacao(@PathVariable Long id, Model model) {
        model.addAttribute("transacaoForm", new TransacaoForm()); // DTO para o form
        model.addAttribute("contaId", id); // ID da conta associada
        return "transacoes/form";
    }

    @PostMapping
    public String salvarTransacao(@ModelAttribute TransacaoForm transacaoForm,
                                @RequestParam("contaId") Long contaId) {

        Conta conta = contaService.buscarPorId(contaId);

        Transacao transacao = new Transacao();
        transacao.setDescricao(transacaoForm.getDescricao());
        transacao.setValor(transacaoForm.getValor());
        transacao.setData(transacaoForm.getData());
        transacao.setConta(conta);

        transacaoService.criarTransacao(transacao);

        return "redirect:/contas/" + contaId + "/transacoes";
    }

    

    
}
