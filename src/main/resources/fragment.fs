#version 330

in vec2 outTexCoord;
in float outLight;

out vec4 fragColor;

uniform sampler2D textureSampler;

void main()
{
    vec4 color = texture(textureSampler, outTexCoord);
    fragColor = vec4(outLight * color.xyz, color.w);
}
