(ns bhlie.webgl.intro)

(defn create-shader [gl type source]
  (let [shader (.createShader gl type)]
    (doto gl (.shaderSource shader source) (.compileShader shader))
    (let [success (.getShaderParameter gl shader gl.COMPILE_STATUS)]
      (if success
        shader
        (do (.log js/console (.getShaderInfoLog gl shader)) (.deleteShader gl shader))))))

(defn create-program [gl vertex-shader fragment-shader]
  (let [program (.createProgram gl)]
    (doto gl 
          (.attachShader program vertex-shader) 
          (.attachShader program fragment-shader)
          (.linkProgram program))
    (let [success (.getProgramParameter gl program gl.LINK_STATUS)]
      (if success
        program
        (do (.log js/console (.getProgramInfoLog gl program)) (.deleteProgram gl program))))))

(defn init []
  (let [canvas (.querySelector js/document "#c")
        gl (.getContext canvas "webgl")]
    (when-not gl (throw (ex-info "WebGL is not enabled in your browser" {})))
    (let [vertex-shader-source (.. js/document (querySelector "#vertex-shader-2d") -text)
          fragment-shader-source (.. js/document (querySelector "#fragment-shader-2d") -text)
          vertex-shader (create-shader gl gl.VERTEX_SHADER vertex-shader-source)
          fragment-shader (create-shader gl gl.FRAGMENT_SHADER fragment-shader-source)
          program (create-program gl vertex-shader fragment-shader)
          position-attribute-location (.getAttribLocation gl program "a_position")
          position-buffer (.createBuffer gl)
          _ (.bindBuffer gl gl.ARRAY_BUFFER position-buffer)
          positions [0 0 0 0.5 0.7 0]]
      (.bufferData gl gl.ARRAY_BUFFER (js/Float32Array. positions) gl.STATIC_DRAW)
      #_(.resizeCanvasToDisplaySize )
      (doto gl
            (.viewport 0 0 (.. gl -canvas -width) (.. gl -canvas -height))
            (.clearColor 0 0 0 0)
            (.clear gl.COLOR_BUFFER_BIT)
            (.useProgram program)
            (.enableVertexAttribArray position-attribute-location)
            (.bindBuffer gl.ARRAY_BUFFER position-buffer))
      (let [size 2 type gl.FLOAT normalize false stride 0 offset 0
            _ (.vertexAttribPointer gl position-attribute-location size type normalize stride offset)
            primitive-type gl.TRIANGLES offset 0 count 3]
        (.drawArrays gl primitive-type offset count)))))

(init)